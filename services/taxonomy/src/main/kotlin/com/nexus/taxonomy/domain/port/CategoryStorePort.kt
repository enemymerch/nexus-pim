package com.nexus.taxonomy.domain.port

import com.nexus.taxonomy.domain.model.Category
import com.nexus.taxonomy.domain.valueobject.CategoryId

interface CategoryStorePort {
    fun save(category: Category): Category
    fun saveAll(categories: List<Category>): List<Category>
    fun findByCode(code: String): Category?
    fun findById(id: CategoryId): Category?
    fun existsByCode(code: String): Boolean
    fun findAllByPathPrefix(pathPrefix: String): List<Category>
}
