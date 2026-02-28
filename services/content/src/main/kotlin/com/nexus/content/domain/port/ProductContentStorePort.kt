package com.nexus.content.domain.port

import com.nexus.content.domain.model.ProductContent

interface ProductContentStorePort {
    fun save(content: ProductContent): ProductContent
    fun findByProductCode(productCode: String): ProductContent?
    fun existsByProductCode(productCode: String): Boolean
}
