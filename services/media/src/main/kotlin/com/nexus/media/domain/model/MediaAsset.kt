package com.nexus.media.domain.model

import com.nexus.media.domain.valueobject.MediaAssetId
import java.time.Instant
import java.util.UUID

enum class MediaType {
    IMAGE,
    DOCUMENT,
    VIDEO,
    OTHER
}

enum class MediaRole {
    MAIN,
    GALLERY,
    MANUAL,
    SWATCH,
    OTHER
}

enum class MediaStatus {
    PENDING_UPLOAD,
    READY,
    DELETED
}

class MediaAsset internal constructor(
    val id: MediaAssetId,
    val productCode: String,
    val variantCode: String?,
    val mediaType: MediaType,
    var role: MediaRole,
    val mimeType: String,
    val originalFilename: String,
    val storageBucket: String,
    val storageKey: String,
    var status: MediaStatus,
    var sizeBytes: Long?,
    var checksumSha256: String?,
    var locale: String?,
    var channel: String?,
    var sortOrder: Int,
    val createdAt: Instant,
    var updatedAt: Instant,
    var uploadedAt: Instant?
) {

    fun markUploaded(sizeBytes: Long?, checksumSha256: String?, now: Instant = Instant.now()) {
        require(status == MediaStatus.PENDING_UPLOAD) { "Only pending assets can be completed." }
        if (sizeBytes != null) {
            require(sizeBytes >= 0L) { "sizeBytes cannot be negative." }
        }
        this.sizeBytes = sizeBytes ?: this.sizeBytes
        this.checksumSha256 = checksumSha256?.trim()?.lowercase()?.takeIf { it.isNotBlank() } ?: this.checksumSha256
        this.status = MediaStatus.READY
        this.uploadedAt = now
        this.updatedAt = now
    }

    fun updatePresentation(
        role: MediaRole,
        locale: String?,
        channel: String?,
        sortOrder: Int,
        now: Instant = Instant.now()
    ) {
        require(status != MediaStatus.DELETED) { "Deleted asset cannot be updated." }
        require(sortOrder >= 0) { "sortOrder cannot be negative." }
        this.role = role
        this.locale = locale?.let(::normalizeLocale)
        this.channel = channel?.let(::normalizeCode)
        this.sortOrder = sortOrder
        this.updatedAt = now
    }

    fun softDelete(now: Instant = Instant.now()) {
        require(status != MediaStatus.DELETED) { "Asset is already deleted." }
        status = MediaStatus.DELETED
        updatedAt = now
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")
        private val LOCALE_REGEX = Regex("^[a-z]{2,3}(-[a-z0-9]{2,8})*$")
        private val MIME_REGEX = Regex("^[a-z0-9!#$&^_.+-]+/[a-z0-9!#$&^_.+-]+$")

        fun createPendingUpload(
            productCode: String,
            variantCode: String?,
            mediaType: MediaType,
            role: MediaRole,
            mimeType: String,
            originalFilename: String,
            storageBucket: String,
            storageKey: String,
            sizeBytes: Long?,
            locale: String?,
            channel: String?,
            sortOrder: Int,
            now: Instant = Instant.now()
        ): MediaAsset {
            val normalizedProductCode = normalizeCode(productCode)
            val normalizedVariantCode = variantCode?.let(::normalizeCode)
            val normalizedMimeType = normalizeMimeType(mimeType)
            val normalizedStorageBucket = storageBucket.trim()
            val normalizedStorageKey = storageKey.trim()
            val normalizedOriginalFilename = normalizeFileName(originalFilename)
            if (sizeBytes != null) {
                require(sizeBytes >= 0L) { "sizeBytes cannot be negative." }
            }
            require(sortOrder >= 0) { "sortOrder cannot be negative." }
            require(normalizedStorageBucket.isNotBlank()) { "storageBucket cannot be blank." }
            require(normalizedStorageKey.isNotBlank()) { "storageKey cannot be blank." }

            return MediaAsset(
                id = MediaAssetId(UUID.randomUUID()),
                productCode = normalizedProductCode,
                variantCode = normalizedVariantCode,
                mediaType = mediaType,
                role = role,
                mimeType = normalizedMimeType,
                originalFilename = normalizedOriginalFilename,
                storageBucket = normalizedStorageBucket,
                storageKey = normalizedStorageKey,
                status = MediaStatus.PENDING_UPLOAD,
                sizeBytes = sizeBytes,
                checksumSha256 = null,
                locale = locale?.let(::normalizeLocale),
                channel = channel?.let(::normalizeCode),
                sortOrder = sortOrder,
                createdAt = now,
                updatedAt = now,
                uploadedAt = null
            )
        }

        fun normalizeCode(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(CODE_REGEX.matches(normalized)) {
                "Invalid code '$normalized'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
            return normalized
        }

        fun normalizeLocale(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(LOCALE_REGEX.matches(normalized)) { "Invalid locale '$normalized'." }
            return normalized
        }

        private fun normalizeMimeType(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(MIME_REGEX.matches(normalized)) { "Invalid mimeType '$normalized'." }
            return normalized
        }

        private fun normalizeFileName(raw: String): String {
            val normalized = raw.trim()
            require(normalized.isNotBlank()) { "originalFilename cannot be blank." }
            require(normalized.length <= 255) { "originalFilename length must be <= 255." }
            return normalized
        }
    }
}
