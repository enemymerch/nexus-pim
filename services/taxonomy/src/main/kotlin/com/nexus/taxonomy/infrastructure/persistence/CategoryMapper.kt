package com.nexus.taxonomy.infrastructure.persistence

import com.nexus.taxonomy.domain.model.Category
import com.nexus.taxonomy.domain.valueobject.CategoryId

object CategoryMapper {

    fun toJpa(category: Category): CategoryJpaEntity =
        CategoryJpaEntity(
            id = category.id.value,
            code = category.code,
            name = category.name,
            parentId = category.parentId?.value,
            depth = category.depth,
            path = category.path,
            createdAt = category.createdAt
        )

    fun toDomain(entity: CategoryJpaEntity): Category =
        Category(
            id = CategoryId(entity.id),
            code = entity.code,
            name = entity.name,
            parentId = entity.parentId?.let(::CategoryId),
            depth = entity.depth,
            path = entity.path,
            createdAt = entity.createdAt
        )
}
