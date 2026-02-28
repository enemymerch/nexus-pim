package com.nexus.taxonomy.infrastructure.adapter

import com.nexus.taxonomy.domain.model.Category
import com.nexus.taxonomy.domain.model.CategoryAttributeSet
import com.nexus.taxonomy.domain.port.CategoryAttributeSetStorePort
import com.nexus.taxonomy.domain.valueobject.CategoryId
import com.nexus.taxonomy.infrastructure.persistence.CategoryAttributeSetJpaEntity
import com.nexus.taxonomy.infrastructure.persistence.CategoryAttributeSetJpaRepository
import com.nexus.taxonomy.infrastructure.persistence.CategoryAttributeSetMapper
import com.nexus.taxonomy.infrastructure.persistence.CategoryJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CategoryAttributeSetStorePostgresAdapter(
    private val mappingRepo: CategoryAttributeSetJpaRepository,
    private val categoryRepo: CategoryJpaRepository
) : CategoryAttributeSetStorePort {

    @Transactional
    override fun replaceForCategory(categoryId: CategoryId, mappings: List<CategoryAttributeSet>) {
        val category = categoryRepo.findById(categoryId.value).orElseThrow {
            NoSuchElementException("Category not found for mapping: ${categoryId.value}")
        }

        mappingRepo.deleteAllByCategoryId(categoryId.value)

        if (mappings.isEmpty()) {
            return
        }

        val entities = mappings
            .map { mapping ->
                val normalizedCode = Category.normalizeCode(mapping.attributeSetCode)
                CategoryAttributeSetJpaEntity(
                    id = mapping.id.value,
                    category = category,
                    attributeSetCode = normalizedCode,
                    createdAt = mapping.createdAt
                )
            }

        mappingRepo.saveAll(entities)
    }

    @Transactional(readOnly = true)
    override fun findByCategoryId(categoryId: CategoryId): List<CategoryAttributeSet> =
        mappingRepo.findAllByCategoryIdOrderByCreatedAtAscAttributeSetCodeAsc(categoryId.value)
            .map(CategoryAttributeSetMapper::toDomain)

    @Transactional(readOnly = true)
    override fun findByCategoryIds(categoryIds: Collection<CategoryId>): List<CategoryAttributeSet> {
        if (categoryIds.isEmpty()) return emptyList()

        return mappingRepo.findAllByCategoryIdInOrderByCreatedAtAscAttributeSetCodeAsc(categoryIds.map { it.value })
            .map(CategoryAttributeSetMapper::toDomain)
    }
}
