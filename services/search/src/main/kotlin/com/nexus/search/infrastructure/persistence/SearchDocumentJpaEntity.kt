package com.nexus.search.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "search_documents")
class SearchDocumentJpaEntity(
    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(name = "product_code", nullable = false, unique = true, length = 64)
    var productCode: String,

    @Column(name = "category_code", nullable = false, length = 64)
    var categoryCode: String,

    @Column(name = "status", nullable = false, length = 32)
    var status: String,

    @Column(name = "default_locale", nullable = false, length = 32)
    var defaultLocale: String,

    @Column(name = "content_title", length = 255)
    var contentTitle: String?,

    @Column(name = "content_description", columnDefinition = "text")
    var contentDescription: String?,

    @Column(name = "media_count", nullable = false)
    var mediaCount: Int,

    @Column(name = "variant_count", nullable = false)
    var variantCount: Int,

    @Column(name = "searchable_text", nullable = false, columnDefinition = "text")
    var searchableText: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
