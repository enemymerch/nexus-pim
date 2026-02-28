package com.nexus.catalog.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "products")
class ProductJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(nullable = false, unique = true, length = 64)
    var code: String,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(name = "category_code", nullable = false, length = 64)
    var categoryCode: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    var status: ProductStatusDb,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)

enum class ProductStatusDb {
    DRAFT,
    ACTIVE,
    ARCHIVED
}
