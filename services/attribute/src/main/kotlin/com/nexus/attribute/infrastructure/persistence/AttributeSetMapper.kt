package com.nexus.attribute.infrastructure.persistence

import com.nexus.attribute.domain.model.AttributeGroup
import com.nexus.attribute.domain.model.AttributeSet
import com.nexus.attribute.domain.model.AttributeSetItem
import com.nexus.attribute.domain.valueobject.AttributeGroupId
import com.nexus.attribute.domain.valueobject.AttributeSetId
import com.nexus.attribute.domain.valueobject.AttributeSetItemId
import java.util.UUID

object AttributeSetMapper {

    /**
     * Create new persistence entities from domain aggregate.
     * NOTE: For updates, adapter will reuse existing JPA IDs and replace children.
     */
    fun toJpaSet(set: AttributeSet): AttributeSetJpaEntity =
        AttributeSetJpaEntity(
            id = set.id.value,
            code = set.code,
            name = set.name,
            createdAt = set.createdAt
        )

    fun toDomain(
        set: AttributeSetJpaEntity,
        groups: List<AttributeGroupJpaEntity>,
        items: List<AttributeSetItemJpaEntity>
    ): AttributeSet {
        // Build empty aggregate, then add children.
        // We need a way to rehydrate keeping same IDs.
        return AttributeSetRehydrator.rehydrate(set, groups, items)
    }

    fun groupToDomain(g: AttributeGroupJpaEntity): AttributeGroup =
        AttributeGroup(
            id = AttributeGroupId(g.id),
            code = g.code,
            name = g.name,
            sortOrder = g.sortOrder,
            createdAt = g.createdAt
        )

    fun itemToDomain(i: AttributeSetItemJpaEntity): AttributeSetItem =
        AttributeSetItem(
            id = AttributeSetItemId(i.id),
            attributeCode = i.attribute.code,
            required = i.required,
            sortOrder = i.sortOrder,
            groupId = i.group?.id?.let(::AttributeGroupId),
            constraintsJson = i.constraintsJson,
            createdAt = i.createdAt
        )
}
