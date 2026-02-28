package com.nexus.catalog.domain.port

import com.nexus.catalog.domain.model.Product

interface ProductStorePort {
    fun save(product: Product): Product
    fun findByCode(code: String): Product?
    fun existsByCode(code: String): Boolean
}
