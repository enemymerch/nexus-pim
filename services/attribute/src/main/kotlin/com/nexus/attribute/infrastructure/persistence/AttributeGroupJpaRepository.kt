package com.nexus.attribute.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttributeGroupJpaRepository: JpaRepository<AttributeGroupJpaEntity, UUID> {
    fun findByAttributeSetIdAndCode(attributeSetId: UUID, code: String): AttributeGroupJpaEntity?
    fun findAllByAttributeSetId(attributeSetId: UUID): List<AttributeGroupJpaEntity>
}