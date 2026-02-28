package com.nexus.taxonomy.application.service

import com.nexus.taxonomy.domain.model.Category
import com.nexus.taxonomy.domain.model.CategoryAttributeSet
import com.nexus.taxonomy.domain.port.CategoryAttributeSetStorePort
import com.nexus.taxonomy.domain.port.CategoryStorePort
import com.nexus.taxonomy.infrastructure.client.AttributeServiceClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TaxonomyService(
    private val categoryStore: CategoryStorePort,
    private val categoryAttributeSetStore: CategoryAttributeSetStorePort,
    private val attributeClient: AttributeServiceClient
) {

    @Transactional
    fun createCategory(code: String, name: String, parentCode: String?): CategoryDetails {
        val normalizedCode = Category.normalizeCode(code)
        Category.validateCode(normalizedCode)

        if (categoryStore.existsByCode(normalizedCode)) {
            throw IllegalArgumentException("Category already exists: $normalizedCode")
        }

        val parent = parentCode?.let { requireCategoryByCode(it) }
        val saved = if (parent == null) {
            categoryStore.save(Category.createRoot(normalizedCode, name))
        } else {
            categoryStore.save(Category.createChild(normalizedCode, name, parent))
        }

        return toDetails(saved)
    }

    @Transactional
    fun updateCategory(code: String, newName: String?, newParentCode: String?, updateParent: Boolean): CategoryDetails {
        val category = requireCategoryByCode(code)

        if (newName != null) {
            category.rename(newName)
        }

        if (updateParent) {
            val newParent = newParentCode?.let { requireCategoryByCode(it) }
            validateParentChange(category, newParent)

            val oldPath = category.path
            val oldDepth = category.depth
            val newPath = buildPath(newParent, category.code)
            val newDepth = newParent?.depth?.plus(1) ?: 0

            category.moveTo(newParent?.id, newPath, newDepth)
            categoryStore.save(category)

            val descendants = categoryStore.findAllByPathPrefix("$oldPath/")
            if (descendants.isNotEmpty()) {
                val depthDelta = newDepth - oldDepth
                descendants.forEach { descendant ->
                    val updatedPath = newPath + descendant.path.removePrefix(oldPath)
                    descendant.moveTo(descendant.parentId, updatedPath, descendant.depth + depthDelta)
                }
                categoryStore.saveAll(descendants)
            }
        } else if (newName != null) {
            categoryStore.save(category)
        }

        return toDetails(category)
    }

    @Transactional
    fun replaceCategoryAttributeSets(categoryCode: String, attributeSetCodes: Set<String>): CategoryDetails {
        val category = requireCategoryByCode(categoryCode)
        val now = Instant.now()
        val normalizedCodes = attributeSetCodes
            .map(Category::normalizeCode)
            .onEach(Category::validateCode)
            .distinct()
            .sorted()
        normalizedCodes.forEach(attributeClient::requireAttributeSetExists)

        val mappings = normalizedCodes.map { code ->
            CategoryAttributeSet(
                categoryId = category.id,
                attributeSetCode = code,
                createdAt = now
            )
        }

        categoryAttributeSetStore.replaceForCategory(category.id, mappings)
        return toDetails(category)
    }

    @Transactional(readOnly = true)
    fun getCategory(categoryCode: String): CategoryDetails =
        toDetails(requireCategoryByCode(categoryCode))

    @Transactional(readOnly = true)
    fun resolveEffectiveAttributeSetCodes(categoryCode: String): List<String> {
        val category = requireCategoryByCode(categoryCode)
        val lineage = lineageRootToLeaf(category)

        val mappingsByCategory = categoryAttributeSetStore.findByCategoryIds(lineage.map { it.id })
            .groupBy { it.categoryId }

        val resolved = linkedSetOf<String>()
        lineage.forEach { node ->
            mappingsByCategory[node.id].orEmpty().forEach { mapping ->
                resolved += mapping.attributeSetCode
            }
        }
        return resolved.toList()
    }

    @Transactional(readOnly = true)
    fun getCategoryMetadata(categoryCode: String): CategoryMetadata {
        val details = getCategory(categoryCode)
        return CategoryMetadata(
            code = details.code,
            name = details.name,
            parentCode = details.parentCode,
            depth = details.depth,
            path = details.path,
            effectiveAttributeSetCodes = details.effectiveAttributeSetCodes
        )
    }

    private fun requireCategoryByCode(code: String): Category {
        val normalized = Category.normalizeCode(code)
        return categoryStore.findByCode(normalized)
            ?: throw NoSuchElementException("Category not found: $normalized")
    }

    private fun buildPath(parent: Category?, code: String): String =
        parent?.let { "${it.path}/$code" } ?: "/$code"

    private fun validateParentChange(category: Category, newParent: Category?) {
        if (newParent == null) {
            return
        }
        if (newParent.id == category.id) {
            throw IllegalArgumentException("Category cannot be parent of itself: ${category.code}")
        }
        if (newParent.path == category.path || newParent.path.startsWith("${category.path}/")) {
            throw IllegalArgumentException(
                "Invalid parent '${newParent.code}' for '${category.code}': cannot move under its own subtree."
            )
        }
    }

    private fun lineageRootToLeaf(category: Category): List<Category> {
        val lineage = mutableListOf<Category>()
        var cursor: Category? = category

        while (cursor != null) {
            lineage += cursor
            cursor = cursor.parentId?.let { parentId ->
                categoryStore.findById(parentId)
                    ?: throw IllegalStateException("Broken category hierarchy: parent not found for ${cursor.code}")
            }
        }

        return lineage.asReversed()
    }

    private fun toDetails(category: Category): CategoryDetails {
        val parentCode = category.parentId?.let { categoryStore.findById(it)?.code }
        val direct = categoryAttributeSetStore.findByCategoryId(category.id)
            .map { it.attributeSetCode }
            .distinct()

        return CategoryDetails(
            code = category.code,
            name = category.name,
            parentCode = parentCode,
            depth = category.depth,
            path = category.path,
            directAttributeSetCodes = direct,
            effectiveAttributeSetCodes = resolveEffectiveAttributeSetCodes(category.code)
        )
    }
}

data class CategoryDetails(
    val code: String,
    val name: String,
    val parentCode: String?,
    val depth: Int,
    val path: String,
    val directAttributeSetCodes: List<String>,
    val effectiveAttributeSetCodes: List<String>
)

data class CategoryMetadata(
    val code: String,
    val name: String,
    val parentCode: String?,
    val depth: Int,
    val path: String,
    val effectiveAttributeSetCodes: List<String>
)
