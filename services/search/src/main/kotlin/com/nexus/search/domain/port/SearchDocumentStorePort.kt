package com.nexus.search.domain.port

import com.nexus.search.domain.model.SearchDocument

interface SearchDocumentStorePort {
    fun save(document: SearchDocument): SearchDocument
    fun findByProductCode(productCode: String): SearchDocument?
    fun search(query: String?, categoryCode: String?, status: String?, limit: Int): List<SearchDocument>
}
