package com.nexus.taxonomy.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryJpaRepository : JpaRepository<CategoryJpaEntity, java.util.UUID> {
    fun findByCode(code: String): CategoryJpaEntity?
    fun existsByCode(code: String): Boolean
    fun findAllByPathStartingWithOrderByDepthAscPathAsc(pathPrefix: String): List<CategoryJpaEntity>
}
