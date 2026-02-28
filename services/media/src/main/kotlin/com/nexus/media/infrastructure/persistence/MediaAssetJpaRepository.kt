package com.nexus.media.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MediaAssetJpaRepository : JpaRepository<MediaAssetJpaEntity, UUID> {
    fun findAllByProductCodeOrderBySortOrderAscCreatedAtAsc(productCode: String): List<MediaAssetJpaEntity>
}
