package com.nexus.search.infrastructure.adapter

import com.nexus.search.domain.model.SearchDocument
import com.nexus.search.domain.port.SearchDocumentStorePort
import com.nexus.search.infrastructure.persistence.SearchDocumentJpaRepository
import com.nexus.search.infrastructure.persistence.SearchDocumentMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class SearchDocumentStorePostgresAdapter(
    private val repository: SearchDocumentJpaRepository
) : SearchDocumentStorePort {

    @Transactional
    override fun save(document: SearchDocument): SearchDocument {
        val existing = repository.findByProductCode(document.productCode)
        val toSave = if (existing != null) {
            existing.apply {
                categoryCode = document.categoryCode
                status = document.status
                defaultLocale = document.defaultLocale
                contentTitle = document.contentTitle
                contentDescription = document.contentDescription
                mediaCount = document.mediaCount
                variantCount = document.variantCount
                searchableText = document.searchableText
                updatedAt = document.updatedAt
            }
        } else {
            SearchDocumentMapper.toJpa(document)
        }
        return SearchDocumentMapper.toDomain(repository.save(toSave))
    }

    @Transactional(readOnly = true)
    override fun findByProductCode(productCode: String): SearchDocument? {
        val normalizedCode = productCode.trim().lowercase()
        return repository.findByProductCode(normalizedCode)?.let(SearchDocumentMapper::toDomain)
    }

    @Transactional(readOnly = true)
    override fun search(query: String?, categoryCode: String?, status: String?, limit: Int): List<SearchDocument> {
        val normalizedQuery = query?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        val normalizedCategoryCode = categoryCode?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        val normalizedStatus = status?.trim()?.uppercase()?.takeIf { it.isNotBlank() }
        val boundedLimit = limit.coerceIn(1, 200)
        return repository.search(normalizedQuery, normalizedCategoryCode, normalizedStatus, boundedLimit)
            .map(SearchDocumentMapper::toDomain)
    }
}
