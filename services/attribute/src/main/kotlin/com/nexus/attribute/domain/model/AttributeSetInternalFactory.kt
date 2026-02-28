package com.nexus.attribute.domain.model

import com.nexus.attribute.domain.valueobject.AttributeSetId
import java.time.Instant

/**
 * Internal factory to rehydrate aggregates from persistence.
 * Keep it in domain; do NOT expose to web layer.
 */
internal object AttributeSetInternalFactory {
    fun rehydrate(
        id: AttributeSetId,
        code: String,
        name: String,
        groups: List<AttributeGroup>,
        items: List<AttributeSetItem>,
        createdAt: Instant
    ): AttributeSet {
        return AttributeSet(
            id = id,
            code = code,
            name = name,
            groups = groups.toMutableList(),
            items = items.toMutableList(),
            createdAt = createdAt
        )
    }
}
