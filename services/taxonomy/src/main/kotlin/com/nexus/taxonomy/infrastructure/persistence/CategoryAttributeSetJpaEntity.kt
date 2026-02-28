package com.nexus.taxonomy.infrastructure.persistence

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
@Table(name = "category_attribute_sets")
class CategoryAttributeSetJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: CategoryJpaEntity,

    @Column(name = "attribute_set_code", nullable = false, length = 64)
    var attributeSetCode: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
