package com.nexus.attribute.domain.model

import com.nexus.attribute.domain.valueobject.AttributeGroupId
import com.nexus.attribute.domain.valueobject.AttributeSetItemId
import java.time.Instant

data class AttributeSetItem(
    val id: AttributeSetItemId,
    val attributeCode: String,
    val required: Boolean,
    val sortOrder: Int,
    val groupId: AttributeGroupId?,
    val constraintsJson: String?,
    val createdAt: Instant
)
