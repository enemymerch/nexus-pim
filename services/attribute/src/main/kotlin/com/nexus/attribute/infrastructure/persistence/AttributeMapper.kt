package com.nexus.attribute.infrastructure.persistence

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.enums.AttributeScope
import com.nexus.attribute.domain.model.Attribute
import java.util.*

object AttributeMapper {
    fun toDomain(e: AttributeJpaEntity): Attribute =
        Attribute.create(
            code = e.code,
            dataType = AttributeDataType.valueOf(e.dataType),
            scope = AttributeScope.valueOf(e.scope),
            localizable = e.localizable,
            channelOverridable = e.channelOverridable,
            multiValue = e.multiValue,
            now = e.createdAt
        )

    fun toEntity(domain: Attribute): AttributeJpaEntity =
        AttributeJpaEntity(
            id = UUID.randomUUID(),
            code = domain.code,
            dataType = domain.dataType.name,
            scope = domain.scope.name,
            localizable = domain.localizable,
            channelOverridable = domain.channelOverridable,
            multiValue = domain.multiValue,
            createdAt = domain.createdAt
        )
}