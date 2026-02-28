package com.nexus.attribute.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttributeJpaRepository: JpaRepository<AttributeJpaEntity, UUID>{
    fun findByCode(code: String): AttributeJpaEntity?
    fun existsByCode(code: String): Boolean
}