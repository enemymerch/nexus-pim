package com.nexus.taxonomy.domain.port

import com.nexus.taxonomy.domain.model.CategoryAttributeSet
import com.nexus.taxonomy.domain.valueobject.CategoryId

interface CategoryAttributeSetStorePort {
    fun replaceForCategory(categoryId: CategoryId, mappings: List<CategoryAttributeSet>)
    fun findByCategoryId(categoryId: CategoryId): List<CategoryAttributeSet>
    fun findByCategoryIds(categoryIds: Collection<CategoryId>): List<CategoryAttributeSet>
}
