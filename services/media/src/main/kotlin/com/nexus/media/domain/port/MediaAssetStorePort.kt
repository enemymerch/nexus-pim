package com.nexus.media.domain.port

import com.nexus.media.domain.model.MediaAsset
import com.nexus.media.domain.valueobject.MediaAssetId

interface MediaAssetStorePort {
    fun save(asset: MediaAsset): MediaAsset
    fun findById(id: MediaAssetId): MediaAsset?
    fun findByProductCode(productCode: String): List<MediaAsset>
}
