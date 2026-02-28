package com.nexus.attribute.domain.model

import com.nexus.attribute.domain.valueobject.AttributeGroupId
import java.time.Instant

data class AttributeGroup(
    val id: AttributeGroupId,
    val code: String,
    val name: String,
    val sortOrder: Int,
    val createdAt: Instant
)
