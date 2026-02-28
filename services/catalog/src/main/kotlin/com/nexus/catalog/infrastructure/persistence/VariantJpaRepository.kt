package com.nexus.catalog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface VariantJpaRepository : JpaRepository<VariantJpaEntity, java.util.UUID> {
    fun findAllByProductId(productId: java.util.UUID): List<VariantJpaEntity>
    fun deleteAllByProductId(productId: java.util.UUID)
}
