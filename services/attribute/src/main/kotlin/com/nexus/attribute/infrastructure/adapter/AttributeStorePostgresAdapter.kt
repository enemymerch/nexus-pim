package com.nexus.attribute.infrastructure.adapter

import com.nexus.attribute.domain.port.AttributeStorePort
import com.nexus.attribute.domain.model.Attribute
import com.nexus.attribute.infrastructure.persistence.AttributeJpaRepository
import com.nexus.attribute.infrastructure.persistence.AttributeMapper
import org.springframework.stereotype.Repository

@Repository
class AttributeStorePostgresAdapter(
    private val jpa: AttributeJpaRepository
) : AttributeStorePort {

    override fun save(attribute: Attribute): Attribute {

        val normalized = attribute.code.trim().lowercase()
        val existing = jpa.findByCode(normalized)

        val entity = existing?.apply {
            dataType = attribute.dataType.name
            scope = attribute.scope.name
            localizable = attribute.localizable
            channelOverridable = attribute.channelOverridable
            multiValue = attribute.multiValue
        }
            ?: AttributeMapper.toEntity(attribute)

        return AttributeMapper.toDomain(jpa.save(entity))
    }

    override fun findByCode(code: String): Attribute? =
        jpa.findByCode(code.trim().lowercase())?.let(AttributeMapper::toDomain)

    override fun existsByCode(code: String): Boolean =
        jpa.existsByCode(code.trim().lowercase())

    override fun findAll(): List<Attribute> =
        jpa.findAll().map(AttributeMapper::toDomain).sortedBy { it.code }
}