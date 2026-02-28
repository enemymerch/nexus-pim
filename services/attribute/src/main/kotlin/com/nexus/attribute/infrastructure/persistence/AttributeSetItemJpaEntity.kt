package com.nexus.attribute.infrastructure.persistence

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "attribute_set_items",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["attribute_set_id", "attribute_id"])
    ]
)
class AttributeSetItemJpaEntity(

    @Id
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_set_id", nullable = false)
    var attributeSet: AttributeSetJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    var attribute: AttributeJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    var group: AttributeGroupJpaEntity?,

    @Column(nullable = false)
    var required: Boolean,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "constraints_json", columnDefinition = "jsonb")
    var constraintsJson: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
