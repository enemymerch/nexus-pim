package com.nexus.attribute.infrastructure.persistence

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "attribute_groups",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["attribute_set_id", "code"])
    ]
)
class AttributeGroupJpaEntity(

    @Id
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_set_id", nullable = false)
    var attributeSet: AttributeSetJpaEntity,

    @Column(nullable = false, length = 64)
    var code: String,

    @Column(nullable = false)
    var name: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
