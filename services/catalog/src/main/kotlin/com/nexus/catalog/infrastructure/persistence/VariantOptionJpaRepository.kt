package com.nexus.catalog.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface VariantOptionJpaRepository : JpaRepository<VariantOptionJpaEntity, java.util.UUID> {
    fun findAllByVariantIdIn(variantIds: Collection<java.util.UUID>): List<VariantOptionJpaEntity>
    fun deleteAllByVariantIdIn(variantIds: Collection<java.util.UUID>)
}
