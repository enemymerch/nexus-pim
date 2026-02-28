package com.nexus.taxonomy.infrastructure.adapter

import com.nexus.taxonomy.domain.model.Category
import com.nexus.taxonomy.domain.port.CategoryStorePort
import com.nexus.taxonomy.domain.valueobject.CategoryId
import com.nexus.taxonomy.infrastructure.persistence.CategoryJpaRepository
import com.nexus.taxonomy.infrastructure.persistence.CategoryMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CategoryStorePostgresAdapter(
    private val categoryRepo: CategoryJpaRepository
) : CategoryStorePort {

    @Transactional
    override fun save(category: Category): Category {
        val existing = categoryRepo.findByCode(category.code)
        val persisted = if (existing != null) {
            existing.apply {
                name = category.name
                parentId = category.parentId?.value
                depth = category.depth
                path = category.path
            }
        } else {
            CategoryMapper.toJpa(category)
        }

        return CategoryMapper.toDomain(categoryRepo.save(persisted))
    }

    @Transactional
    override fun saveAll(categories: List<Category>): List<Category> {
        if (categories.isEmpty()) return emptyList()

        val entities = categories.map { category ->
            val existing = categoryRepo.findByCode(category.code)
            if (existing != null) {
                existing.apply {
                    name = category.name
                    parentId = category.parentId?.value
                    depth = category.depth
                    path = category.path
                }
            } else {
                CategoryMapper.toJpa(category)
            }
        }

        return categoryRepo.saveAll(entities).map(CategoryMapper::toDomain)
    }

    @Transactional(readOnly = true)
    override fun findByCode(code: String): Category? =
        categoryRepo.findByCode(code.trim().lowercase())?.let(CategoryMapper::toDomain)

    @Transactional(readOnly = true)
    override fun findById(id: CategoryId): Category? =
        categoryRepo.findById(id.value).orElse(null)?.let(CategoryMapper::toDomain)

    @Transactional(readOnly = true)
    override fun existsByCode(code: String): Boolean =
        categoryRepo.existsByCode(code.trim().lowercase())

    @Transactional(readOnly = true)
    override fun findAllByPathPrefix(pathPrefix: String): List<Category> {
        val normalizedPrefix = pathPrefix.trim().lowercase()
        return categoryRepo.findAllByPathStartingWithOrderByDepthAscPathAsc(normalizedPrefix)
            .map(CategoryMapper::toDomain)
    }
}
