package com.nexus.attribute.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AttributeSetJpaRepository : JpaRepository<AttributeSetJpaEntity, UUID> {
    fun findByCode(code: String): AttributeSetJpaEntity?
    fun existsByCode(code: String): Boolean
}