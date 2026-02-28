package com.nexus.taxonomy.domain.model

import com.nexus.taxonomy.domain.valueobject.CategoryAttributeSetId
import com.nexus.taxonomy.domain.valueobject.CategoryId
import java.time.Instant
import java.util.UUID

data class CategoryAttributeSet(
    val id: CategoryAttributeSetId = CategoryAttributeSetId(UUID.randomUUID()),
    val categoryId: CategoryId,
    val attributeSetCode: String,
    val createdAt: Instant
)
