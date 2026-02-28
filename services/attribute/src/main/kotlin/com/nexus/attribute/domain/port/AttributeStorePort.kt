package com.nexus.attribute.domain.port

import com.nexus.attribute.domain.model.Attribute

interface AttributeStorePort {
    fun save(attribute: Attribute): Attribute
    fun findByCode(code: String): Attribute?
    fun existsByCode(code: String): Boolean
    fun findAll(): List<Attribute>
}