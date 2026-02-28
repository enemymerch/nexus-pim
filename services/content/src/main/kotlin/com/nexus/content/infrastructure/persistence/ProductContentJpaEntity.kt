package com.nexus.content.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "product_contents")
class ProductContentJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(name = "product_code", nullable = false, unique = true, length = 64)
    var productCode: String,

    @Column(name = "default_locale", nullable = false, length = 32)
    var defaultLocale: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
