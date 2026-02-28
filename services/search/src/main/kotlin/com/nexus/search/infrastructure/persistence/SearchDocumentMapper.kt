package com.nexus.search.infrastructure.persistence

import com.nexus.search.domain.model.SearchDocument
import com.nexus.search.domain.valueobject.SearchDocumentId

object SearchDocumentMapper {
    fun toJpa(document: SearchDocument): SearchDocumentJpaEntity =
        SearchDocumentJpaEntity(
            id = document.id.value,
            productCode = document.productCode,
            categoryCode = document.categoryCode,
            status = document.status,
            defaultLocale = document.defaultLocale,
            contentTitle = document.contentTitle,
            contentDescription = document.contentDescription,
            mediaCount = document.mediaCount,
            variantCount = document.variantCount,
            searchableText = document.searchableText,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt
        )

    fun toDomain(entity: SearchDocumentJpaEntity): SearchDocument =
        SearchDocument(
            id = SearchDocumentId(entity.id),
            productCode = entity.productCode,
            categoryCode = entity.categoryCode,
            status = entity.status,
            defaultLocale = entity.defaultLocale,
            contentTitle = entity.contentTitle,
            contentDescription = entity.contentDescription,
            mediaCount = entity.mediaCount,
            variantCount = entity.variantCount,
            searchableText = entity.searchableText,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
}
