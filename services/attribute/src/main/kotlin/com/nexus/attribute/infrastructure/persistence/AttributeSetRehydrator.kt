package com.nexus.attribute.infrastructure.persistence

import com.nexus.attribute.domain.model.AttributeSet
import com.nexus.attribute.domain.model.AttributeSetInternalFactory
import com.nexus.attribute.domain.valueobject.AttributeSetId

internal object AttributeSetRehydrator {

    fun rehydrate(
        set: AttributeSetJpaEntity,
        groups: List<AttributeGroupJpaEntity>,
        items: List<AttributeSetItemJpaEntity>
    ): AttributeSet {
        // Use reflection-free approach: create a new set via create(), then overwrite ids via a special factory.
        // We'll implement a dedicated internal factory in domain to allow rehydration.
        return AttributeSetInternalFactory.rehydrate(
            id = AttributeSetId(set.id),
            code = set.code,
            name = set.name,
            groups = groups.map(AttributeSetMapper::groupToDomain),
            items = items.map(AttributeSetMapper::itemToDomain),
            createdAt = set.createdAt
        )
    }
}
