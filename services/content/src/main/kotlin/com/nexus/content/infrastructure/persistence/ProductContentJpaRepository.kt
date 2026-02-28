package com.nexus.content.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProductContentJpaRepository : JpaRepository<ProductContentJpaEntity, java.util.UUID> {
    fun findByProductCode(productCode: String): ProductContentJpaEntity?
    fun existsByProductCode(productCode: String): Boolean
}
