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
@Table(name = "product_variant_attributes")
class ProductVariantAttributeJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: ProductJpaEntity,

    @Column(name = "attribute_code", nullable = false, length = 64)
    var attributeCode: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
