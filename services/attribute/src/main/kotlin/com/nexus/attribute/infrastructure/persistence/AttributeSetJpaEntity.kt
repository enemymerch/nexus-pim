package com.nexus.attribute.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "attribute_sets")
class AttributeSetJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(nullable = false, unique = true, length = 64)
    var code: String,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)