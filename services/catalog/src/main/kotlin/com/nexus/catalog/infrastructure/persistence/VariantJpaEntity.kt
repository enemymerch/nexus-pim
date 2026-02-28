package com.nexus.catalog.infrastructure.persistence

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
@Table(name = "skus")
class VariantJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: ProductJpaEntity,

    @Column(nullable = false, length = 64)
    var code: String,

    @Column(nullable = false, length = 1024)
    var signature: String,

    @Column(nullable = false)
    var active: Boolean,

    @Column(nullable = true, length = 128)
    var barcode: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
