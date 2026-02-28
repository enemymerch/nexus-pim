package com.nexus.search.application.service

import com.nexus.search.domain.model.SearchDocument
import com.nexus.search.domain.port.SearchDocumentStorePort
import com.nexus.search.infrastructure.client.CatalogServiceClient
import com.nexus.search.infrastructure.client.ContentServiceClient
import com.nexus.search.infrastructure.client.MediaServiceClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SearchService(
    private val store: SearchDocumentStorePort,
    private val catalogClient: CatalogServiceClient,
    private val contentClient: ContentServiceClient,
    private val mediaClient: MediaServiceClient
) {

    @Transactional
    fun reindexProduct(productCode: String, locale: String = "en-us", channel: String? = null): SearchDocumentDetails {
        val normalizedCode = SearchDocument.normalizeCode(productCode)

        val product = catalogClient.getProduct(normalizedCode)
        val resolvedContent = contentClient.resolve(normalizedCode, locale, channel)
        val mediaAssets = mediaClient.listByProduct(normalizedCode)
        val readyMediaCount = mediaAssets.count { it.status.equals("READY", ignoreCase = true) }

        val existing = store.findByProductCode(normalizedCode)
        val document = if (existing == null) {
            SearchDocument.create(
                productCode = normalizedCode,
                categoryCode = product.categoryCode,
                status = product.status,
                defaultLocale = resolvedContent?.defaultLocale ?: locale,
                contentTitle = resolvedContent?.title,
                contentDescription = resolvedContent?.description,
                mediaCount = readyMediaCount,
                variantCount = product.variants.size
            )
        } else {
            existing.refresh(
                categoryCode = product.categoryCode,
                status = product.status,
                defaultLocale = resolvedContent?.defaultLocale ?: locale,
                contentTitle = resolvedContent?.title,
                contentDescription = resolvedContent?.description,
                mediaCount = readyMediaCount,
                variantCount = product.variants.size
            )
            existing
        }
        return toDetails(store.save(document))
    }

    @Transactional(readOnly = true)
    fun getDocument(productCode: String): SearchDocumentDetails {
        val normalizedCode = SearchDocument.normalizeCode(productCode)
        val document = store.findByProductCode(normalizedCode)
            ?: throw NoSuchElementException("Search document not found for product: $normalizedCode")
        return toDetails(document)
    }

    @Transactional(readOnly = true)
    fun search(query: String?, categoryCode: String?, status: String?, limit: Int): List<SearchDocumentDetails> =
        store.search(query, categoryCode, status, limit).map(::toDetails)

    private fun toDetails(document: SearchDocument): SearchDocumentDetails =
        SearchDocumentDetails(
            productCode = document.productCode,
            categoryCode = document.categoryCode,
            status = document.status,
            defaultLocale = document.defaultLocale,
            contentTitle = document.contentTitle,
            contentDescription = document.contentDescription,
            mediaCount = document.mediaCount,
            variantCount = document.variantCount,
            updatedAt = document.updatedAt
        )
}

data class SearchDocumentDetails(
    val productCode: String,
    val categoryCode: String,
    val status: String,
    val defaultLocale: String,
    val contentTitle: String?,
    val contentDescription: String?,
    val mediaCount: Int,
    val variantCount: Int,
    val updatedAt: Instant
)
