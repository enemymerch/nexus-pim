package com.nexus.taxonomy.infrastructure.persistence

import com.nexus.taxonomy.domain.model.CategoryAttributeSet
import com.nexus.taxonomy.domain.valueobject.CategoryAttributeSetId
import com.nexus.taxonomy.domain.valueobject.CategoryId

object CategoryAttributeSetMapper {

    fun toDomain(entity: CategoryAttributeSetJpaEntity): CategoryAttributeSet =
        CategoryAttributeSet(
            id = CategoryAttributeSetId(entity.id),
            categoryId = CategoryId(entity.category.id),
            attributeSetCode = entity.attributeSetCode,
            createdAt = entity.createdAt
        )
}
