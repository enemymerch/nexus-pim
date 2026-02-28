package com.nexus.catalog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductJpaEntity, java.util.UUID> {
    fun findByCode(code: String): ProductJpaEntity?
    fun existsByCode(code: String): Boolean
}
