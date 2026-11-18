package com.nexus.media.application.service

import com.nexus.media.domain.model.MediaAsset
import com.nexus.media.domain.model.MediaRole
import com.nexus.media.domain.model.MediaType
import com.nexus.media.domain.port.MediaAssetStorePort
import com.nexus.media.domain.valueobject.MediaAssetId
import com.nexus.media.infrastructure.client.CatalogServiceClient
import com.nexus.media.infrastructure.messaging.ProductSearchIndexEventPublisher
import com.nexus.media.infrastructure.storage.UploadUrlFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class MediaService(
    private val store: MediaAssetStorePort,
    private val catalogClient: CatalogServiceClient,
    private val uploadUrlFactory: UploadUrlFactory,
    private val indexEventPublisher: ProductSearchIndexEventPublisher
) {

    @Transactional
    fun initiateUpload(
        productCode: String,
        variantCode: String?,
        mediaType: MediaType,
        role: MediaRole,
        mimeType: String,
        originalFilename: String,
        sizeBytes: Long?,
        locale: String?,
        channel: String?,
        sortOrder: Int
    ): InitiatedUploadDetails {
        val normalizedProductCode = MediaAsset.normalizeCode(productCode)
        val normalizedVariantCode = variantCode?.let(MediaAsset::normalizeCode)

        catalogClient.requireProductExists(normalizedProductCode, normalizedVariantCode)

        val mediaId = MediaAssetId(UUID.randomUUID())
        val storageKey = buildStorageKey(
            productCode = normalizedProductCode,
            mediaId = mediaId,
            originalFilename = originalFilename
        )

        val now = Instant.now()
        val pending = MediaAsset.createPendingUpload(
            productCode = normalizedProductCode,
            variantCode = normalizedVariantCode,
            mediaType = mediaType,
            role = role,
            mimeType = mimeType,
            originalFilename = originalFilename,
            storageBucket = uploadUrlFactory.bucket(),
            storageKey = storageKey,
            sizeBytes = sizeBytes,
            locale = locale,
            channel = channel,
            sortOrder = sortOrder,
            now = now
        )

        // Reuse pre-generated ID so key and DB ID stay aligned.
        val toSave = MediaAsset(
            id = mediaId,
            productCode = pending.productCode,
            variantCode = pending.variantCode,
            mediaType = pending.mediaType,
            role = pending.role,
            mimeType = pending.mimeType,
            originalFilename = pending.originalFilename,
            storageBucket = pending.storageBucket,
            storageKey = pending.storageKey,
            status = pending.status,
            sizeBytes = pending.sizeBytes,
            checksumSha256 = pending.checksumSha256,
            locale = pending.locale,
            channel = pending.channel,
            sortOrder = pending.sortOrder,
            createdAt = pending.createdAt,
            updatedAt = pending.updatedAt,
            uploadedAt = pending.uploadedAt
        )

        val saved = store.save(toSave)
        indexEventPublisher.publish(saved.productCode, "MEDIA_UPLOAD_INITIATED", saved.locale, saved.channel)
        return InitiatedUploadDetails(
            asset = toDetails(saved),
            uploadUrl = uploadUrlFactory.createUploadUrl(saved.storageKey),
            expiresAt = Instant.now().plusSeconds(uploadUrlFactory.ttlSeconds())
        )
    }

    @Transactional
    fun completeUpload(assetId: UUID, sizeBytes: Long?, checksumSha256: String?): MediaAssetDetails {
        val id = MediaAssetId(assetId)
        val asset = requireAsset(id)
        asset.markUploaded(sizeBytes = sizeBytes, checksumSha256 = checksumSha256)
        val saved = store.save(asset)
        indexEventPublisher.publish(saved.productCode, "MEDIA_UPLOAD_COMPLETED", saved.locale, saved.channel)
        return toDetails(saved)
    }

    @Transactional
    fun updatePresentation(
        assetId: UUID,
        role: MediaRole,
        locale: String?,
        channel: String?,
        sortOrder: Int
    ): MediaAssetDetails {
        val id = MediaAssetId(assetId)
        val asset = requireAsset(id)
        asset.updatePresentation(role = role, locale = locale, channel = channel, sortOrder = sortOrder)
        val saved = store.save(asset)
        indexEventPublisher.publish(saved.productCode, "MEDIA_PRESENTATION_UPDATED", saved.locale, saved.channel)
        return toDetails(saved)
    }

    @Transactional(readOnly = true)
    fun getAsset(assetId: UUID): MediaAssetDetails {
        val asset = requireAsset(MediaAssetId(assetId))
        return toDetails(asset)
    }

    @Transactional(readOnly = true)
    fun listByProduct(productCode: String): List<MediaAssetDetails> {
        val normalizedProductCode = MediaAsset.normalizeCode(productCode)
        return store.findByProductCode(normalizedProductCode).map(::toDetails)
    }

    @Transactional
    fun deleteAsset(assetId: UUID) {
        val asset = requireAsset(MediaAssetId(assetId))
        asset.softDelete()
        val saved = store.save(asset)
        indexEventPublisher.publish(saved.productCode, "MEDIA_DELETED", saved.locale, saved.channel)
    }

    private fun requireAsset(id: MediaAssetId): MediaAsset =
        store.findById(id) ?: throw NoSuchElementException("Media asset not found: ${id.value}")

    private fun buildStorageKey(productCode: String, mediaId: MediaAssetId, originalFilename: String): String {
        val cleaned = originalFilename.trim().replace(" ", "_")
        return "products/$productCode/${mediaId.value}/$cleaned"
    }

    private fun toDetails(asset: MediaAsset): MediaAssetDetails =
        MediaAssetDetails(
            id = asset.id.value,
            productCode = asset.productCode,
            variantCode = asset.variantCode,
            mediaType = asset.mediaType.name,
            role = asset.role.name,
            status = asset.status.name,
            mimeType = asset.mimeType,
            originalFilename = asset.originalFilename,
            storageBucket = asset.storageBucket,
            storageKey = asset.storageKey,
            downloadUrl = uploadUrlFactory.createDownloadUrl(asset.storageKey),
            sizeBytes = asset.sizeBytes,
            checksumSha256 = asset.checksumSha256,
            locale = asset.locale,
            channel = asset.channel,
            sortOrder = asset.sortOrder,
            createdAt = asset.createdAt,
            updatedAt = asset.updatedAt,
            uploadedAt = asset.uploadedAt
        )
}

data class MediaAssetDetails(
    val id: UUID,
    val productCode: String,
    val variantCode: String?,
    val mediaType: String,
    val role: String,
    val status: String,
    val mimeType: String,
    val originalFilename: String,
    val storageBucket: String,
    val storageKey: String,
    val downloadUrl: String,
    val sizeBytes: Long?,
    val checksumSha256: String?,
    val locale: String?,
    val channel: String?,
    val sortOrder: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val uploadedAt: Instant?
)

data class InitiatedUploadDetails(
    val asset: MediaAssetDetails,
    val uploadUrl: String,
    val expiresAt: Instant
)
