package com.nexus.content.infrastructure.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nexus.content.domain.model.LocalizedContent
import com.nexus.content.domain.model.ProductContent
import com.nexus.content.domain.valueobject.ContentEntryId
import com.nexus.content.domain.valueobject.ProductContentId

object ProductContentMapper {

    fun toJpa(content: ProductContent): ProductContentJpaEntity =
        ProductContentJpaEntity(
            id = content.id.value,
            productCode = content.productCode,
            defaultLocale = content.defaultLocale,
            createdAt = content.createdAt,
            updatedAt = content.updatedAt
        )

    fun toDomain(
        root: ProductContentJpaEntity,
        entries: List<ContentEntryJpaEntity>,
        objectMapper: ObjectMapper
    ): ProductContent =
        ProductContent(
            id = ProductContentId(root.id),
            productCode = root.productCode,
            defaultLocale = root.defaultLocale,
            entries = entries.map { entry ->
                LocalizedContent(
                    id = ContentEntryId(entry.id),
                    locale = entry.locale,
                    channel = entry.channel,
                    title = entry.title,
                    shortDescription = entry.shortDescription,
                    description = entry.description,
                    bullets = objectMapper.readValue(entry.bulletsJson, object : TypeReference<List<String>>() {}),
                    createdAt = entry.createdAt,
                    updatedAt = entry.updatedAt
                )
            }.toMutableList(),
            createdAt = root.createdAt,
            updatedAt = root.updatedAt
        )
}
