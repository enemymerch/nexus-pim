package com.nexus.media.infrastructure.adapter

import com.nexus.media.domain.model.MediaAsset
import com.nexus.media.domain.port.MediaAssetStorePort
import com.nexus.media.domain.valueobject.MediaAssetId
import com.nexus.media.infrastructure.persistence.MediaAssetJpaRepository
import com.nexus.media.infrastructure.persistence.MediaAssetMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class MediaAssetStorePostgresAdapter(
    private val repository: MediaAssetJpaRepository
) : MediaAssetStorePort {

    @Transactional
    override fun save(asset: MediaAsset): MediaAsset {
        val saved = repository.save(MediaAssetMapper.toJpa(asset))
        return MediaAssetMapper.toDomain(saved)
    }

    @Transactional(readOnly = true)
    override fun findById(id: MediaAssetId): MediaAsset? =
        repository.findById(id.value).map(MediaAssetMapper::toDomain).orElse(null)

    @Transactional(readOnly = true)
    override fun findByProductCode(productCode: String): List<MediaAsset> {
        val normalizedCode = productCode.trim().lowercase()
        return repository.findAllByProductCodeOrderBySortOrderAscCreatedAtAsc(normalizedCode)
            .map(MediaAssetMapper::toDomain)
    }
}
