package com.nexus.media.web.dto

import com.nexus.media.application.service.InitiatedUploadDetails
import com.nexus.media.application.service.MediaAssetDetails
import com.nexus.media.domain.model.MediaRole
import com.nexus.media.domain.model.MediaType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.UUID

data class InitiateUploadRequest(
    @field:NotBlank val productCode: String,
    val variantCode: String? = null,
    @field:NotNull val mediaType: MediaType,
    @field:NotNull val role: MediaRole,
    @field:NotBlank val mimeType: String,
    @field:NotBlank val originalFilename: String,
    @field:Min(0) val sizeBytes: Long? = null,
    val locale: String? = null,
    val channel: String? = null,
    @field:Min(0) val sortOrder: Int = 0
)

data class CompleteUploadRequest(
    @field:Min(0) val sizeBytes: Long? = null,
    val checksumSha256: String? = null
)

data class UpdatePresentationRequest(
    @field:NotNull val role: MediaRole,
    val locale: String? = null,
    val channel: String? = null,
    @field:Min(0) val sortOrder: Int = 0
)

data class MediaAssetResponse(
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

data class InitiatedUploadResponse(
    val asset: MediaAssetResponse,
    val uploadUrl: String,
    val expiresAt: Instant
)

fun MediaAssetDetails.toResponse(): MediaAssetResponse =
    MediaAssetResponse(
        id = id,
        productCode = productCode,
        variantCode = variantCode,
        mediaType = mediaType,
        role = role,
        status = status,
        mimeType = mimeType,
        originalFilename = originalFilename,
        storageBucket = storageBucket,
        storageKey = storageKey,
        downloadUrl = downloadUrl,
        sizeBytes = sizeBytes,
        checksumSha256 = checksumSha256,
        locale = locale,
        channel = channel,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
        uploadedAt = uploadedAt
    )

fun InitiatedUploadDetails.toResponse(): InitiatedUploadResponse =
    InitiatedUploadResponse(
        asset = asset.toResponse(),
        uploadUrl = uploadUrl,
        expiresAt = expiresAt
    )
