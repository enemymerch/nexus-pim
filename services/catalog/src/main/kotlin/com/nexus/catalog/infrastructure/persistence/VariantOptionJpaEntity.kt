package com.nexus.catalog.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sku_options")
class VariantOptionJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    var variant: VariantJpaEntity,

    @Column(name = "attribute_code", nullable = false, length = 64)
    var attributeCode: String,

    @Column(name = "attribute_value_code", nullable = false, length = 64)
    var attributeValueCode: String
)
