package com.nexus.content.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "content_entries")
class ContentEntryJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_content_id", nullable = false)
    var productContent: ProductContentJpaEntity,

    @Column(nullable = false, length = 32)
    var locale: String,

    @Column(nullable = true, length = 64)
    var channel: String?,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(name = "short_description", length = 500)
    var shortDescription: String?,

    @Column(columnDefinition = "text")
    var description: String?,

    @Column(name = "bullets_json", columnDefinition = "jsonb", nullable = false)
    var bulletsJson: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
