package com.nexus.attribute.domain.port

import com.nexus.attribute.domain.model.AttributeSet

interface AttributeSetStorePort {
    fun save(set: AttributeSet): AttributeSet
    fun findByCode(code: String): AttributeSet?
    fun existsByCode(code: String): Boolean
}
