package com.nexus.taxonomy.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryAttributeSetJpaRepository : JpaRepository<CategoryAttributeSetJpaEntity, java.util.UUID> {
    fun deleteAllByCategoryId(categoryId: java.util.UUID)
    fun findAllByCategoryIdOrderByCreatedAtAscAttributeSetCodeAsc(categoryId: java.util.UUID): List<CategoryAttributeSetJpaEntity>
    fun findAllByCategoryIdInOrderByCreatedAtAscAttributeSetCodeAsc(categoryIds: Collection<java.util.UUID>): List<CategoryAttributeSetJpaEntity>
}
