package com.nexus.attribute.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "attributes")
class AttributeJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(nullable = false, unique = true, length = 64)
    var code: String,

    @Column(name = "data_type", nullable = false, length = 32)
    var dataType: String,

    @Column(nullable = false, length = 32)
    var scope: String,

    @Column(nullable = false)
    var localizable: Boolean,

    @Column(name = "channel_overridable", nullable = false)
    var channelOverridable: Boolean,

    @Column(name = "multi_value", nullable = false)
    var multiValue: Boolean,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)

