package com.nexus.media.infrastructure.persistence

import com.nexus.media.domain.model.MediaRole
import com.nexus.media.domain.model.MediaStatus
import com.nexus.media.domain.model.MediaType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "media_assets")
class MediaAssetJpaEntity(
    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(name = "product_code", nullable = false, length = 64)
    var productCode: String,

    @Column(name = "variant_code", length = 64)
    var variantCode: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 16)
    var mediaType: MediaType,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    var role: MediaRole,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    var status: MediaStatus,

    @Column(name = "mime_type", nullable = false, length = 128)
    var mimeType: String,

    @Column(name = "original_filename", nullable = false, length = 255)
    var originalFilename: String,

    @Column(name = "storage_bucket", nullable = false, length = 128)
    var storageBucket: String,

    @Column(name = "storage_key", nullable = false, unique = true, length = 512)
    var storageKey: String,

    @Column(name = "size_bytes")
    var sizeBytes: Long?,

    @Column(name = "checksum_sha256", length = 64)
    var checksumSha256: String?,

    @Column(name = "locale", length = 32)
    var locale: String?,

    @Column(name = "channel", length = 64)
    var channel: String?,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant,

    @Column(name = "uploaded_at")
    var uploadedAt: Instant?
)
