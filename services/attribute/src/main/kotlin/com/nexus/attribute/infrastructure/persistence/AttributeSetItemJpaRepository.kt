package com.nexus.attribute.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttributeSetItemJpaRepository: JpaRepository<AttributeSetItemJpaEntity, UUID> {
    fun existsByAttributeSetIdAndAttributeId(attributeSetId: UUID, attributeId: UUID): Boolean
    fun findAllByAttributeSetId(attributeSetId: UUID): List<AttributeSetItemJpaEntity>
}