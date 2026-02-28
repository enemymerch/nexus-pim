package com.nexus.media.infrastructure.persistence

import com.nexus.media.domain.model.MediaAsset
import com.nexus.media.domain.valueobject.MediaAssetId

object MediaAssetMapper {
    fun toJpa(asset: MediaAsset): MediaAssetJpaEntity =
        MediaAssetJpaEntity(
            id = asset.id.value,
            productCode = asset.productCode,
            variantCode = asset.variantCode,
            mediaType = asset.mediaType,
            role = asset.role,
            status = asset.status,
            mimeType = asset.mimeType,
            originalFilename = asset.originalFilename,
            storageBucket = asset.storageBucket,
            storageKey = asset.storageKey,
            sizeBytes = asset.sizeBytes,
            checksumSha256 = asset.checksumSha256,
            locale = asset.locale,
            channel = asset.channel,
            sortOrder = asset.sortOrder,
            createdAt = asset.createdAt,
            updatedAt = asset.updatedAt,
            uploadedAt = asset.uploadedAt
        )

    fun toDomain(entity: MediaAssetJpaEntity): MediaAsset =
        MediaAsset(
            id = MediaAssetId(entity.id),
            productCode = entity.productCode,
            variantCode = entity.variantCode,
            mediaType = entity.mediaType,
            role = entity.role,
            mimeType = entity.mimeType,
            originalFilename = entity.originalFilename,
            storageBucket = entity.storageBucket,
            storageKey = entity.storageKey,
            status = entity.status,
            sizeBytes = entity.sizeBytes,
            checksumSha256 = entity.checksumSha256,
            locale = entity.locale,
            channel = entity.channel,
            sortOrder = entity.sortOrder,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            uploadedAt = entity.uploadedAt
        )
}
