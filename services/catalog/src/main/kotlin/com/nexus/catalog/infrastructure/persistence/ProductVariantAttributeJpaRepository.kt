package com.nexus.catalog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProductVariantAttributeJpaRepository : JpaRepository<ProductVariantAttributeJpaEntity, java.util.UUID> {
    fun findAllByProductId(productId: java.util.UUID): List<ProductVariantAttributeJpaEntity>
    fun deleteAllByProductId(productId: java.util.UUID)
}
