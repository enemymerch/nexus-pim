package com.nexus.search.infrastructure.adapter

import com.nexus.search.domain.model.SearchDocument
import com.nexus.search.domain.port.SearchDocumentStorePort
import com.nexus.search.domain.valueobject.SearchDocumentId
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import java.time.Instant
import java.util.UUID

@Repository
class SearchDocumentStorePostgresAdapter(
    @Value("\${integration.elasticsearch.base-url:http://localhost:9200}") baseUrl: String,
    @Value("\${search.index-name:nexus_product_search_v1}") private val indexName: String,
    builder: RestClient.Builder,
    private val objectMapper: ObjectMapper
) : SearchDocumentStorePort {
    private val client = builder.baseUrl(baseUrl).build()

    @PostConstruct
    fun ensureIndex() {
        if (indexExists()) return

        val body = mapOf(
            "mappings" to mapOf(
                "properties" to mapOf(
                    "productCode" to mapOf("type" to "keyword"),
                    "categoryCode" to mapOf("type" to "keyword"),
                    "status" to mapOf("type" to "keyword"),
                    "defaultLocale" to mapOf("type" to "keyword"),
                    "contentTitle" to mapOf("type" to "text"),
                    "contentDescription" to mapOf("type" to "text"),
                    "searchableText" to mapOf("type" to "text"),
                    "mediaCount" to mapOf("type" to "integer"),
                    "variantCount" to mapOf("type" to "integer"),
                    "createdAt" to mapOf("type" to "date"),
                    "updatedAt" to mapOf("type" to "date")
                )
            )
        )

        client.put()
            .uri("/$indexName")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .toBodilessEntity()
    }

    @Transactional
    override fun save(document: SearchDocument): SearchDocument {
        val payload = mapOf(
            "id" to document.id.value.toString(),
            "productCode" to document.productCode,
            "categoryCode" to document.categoryCode,
            "status" to document.status,
            "defaultLocale" to document.defaultLocale,
            "contentTitle" to document.contentTitle,
            "contentDescription" to document.contentDescription,
            "mediaCount" to document.mediaCount,
            "variantCount" to document.variantCount,
            "searchableText" to document.searchableText,
            "createdAt" to document.createdAt.toString(),
            "updatedAt" to document.updatedAt.toString()
        )

        client.put()
            .uri("/$indexName/_doc/{id}", document.productCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .retrieve()
            .toBodilessEntity()

        return document
    }

    @Transactional(readOnly = true)
    override fun findByProductCode(productCode: String): SearchDocument? {
        val normalizedCode = productCode.trim().lowercase()
        val body = try {
            client.get()
                .uri("/$indexName/_doc/{id}", normalizedCode)
                .retrieve()
                .body(String::class.java)
                ?: return null
        } catch (ex: RestClientResponseException) {
            if (ex.statusCode.value() == 404) return null
            throw ex
        }

        val source = objectMapper.readTree(body).get("_source") ?: return null
        return source.toDomain()
    }

    @Transactional(readOnly = true)
    override fun search(query: String?, categoryCode: String?, status: String?, limit: Int): List<SearchDocument> {
        val normalizedQuery = query?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        val normalizedCategoryCode = categoryCode?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
        val normalizedStatus = status?.trim()?.uppercase()?.takeIf { it.isNotBlank() }
        val boundedLimit = limit.coerceIn(1, 200)

        val filters = mutableListOf<Map<String, Any>>()
        if (normalizedCategoryCode != null) {
            filters += mapOf("term" to mapOf("categoryCode" to normalizedCategoryCode))
        }
        if (normalizedStatus != null) {
            filters += mapOf("term" to mapOf("status" to normalizedStatus))
        }

        val must = mutableListOf<Map<String, Any>>()
        if (normalizedQuery != null) {
            must += mapOf(
                "simple_query_string" to mapOf(
                    "query" to normalizedQuery,
                    "fields" to listOf("searchableText", "contentTitle", "contentDescription")
                )
            )
        }

        val searchBody = mapOf(
            "size" to boundedLimit,
            "query" to mapOf(
                "bool" to mapOf(
                    "must" to must,
                    "filter" to filters
                )
            ),
            "sort" to listOf(mapOf("updatedAt" to mapOf("order" to "desc")))
        )

        val response = client.post()
            .uri("/$indexName/_search")
            .contentType(MediaType.APPLICATION_JSON)
            .body(searchBody)
            .retrieve()
            .body(String::class.java)
            ?: return emptyList()

        val root = objectMapper.readTree(response)
        return root.path("hits").path("hits")
            .mapNotNull { it.path("_source").takeIf(JsonNode::isObject)?.toDomain() }
    }

    private fun indexExists(): Boolean =
        try {
            client.get()
                .uri("/$indexName")
                .retrieve()
                .toBodilessEntity()
            true
        } catch (ex: RestClientResponseException) {
            if (ex.statusCode.value() == 404) false else throw ex
        }

    private fun JsonNode.toDomain(): SearchDocument {
        val idText = path("id").asText()
        val createdAtText = path("createdAt").asText()
        val updatedAtText = path("updatedAt").asText()
        val createdAt = createdAtText.takeIf { it.isNotBlank() }?.let(Instant::parse) ?: Instant.now()
        val updatedAt = updatedAtText.takeIf { it.isNotBlank() }?.let(Instant::parse) ?: createdAt

        return SearchDocument(
            id = SearchDocumentId(idText.takeIf { it.isNotBlank() }?.let(UUID::fromString) ?: UUID.randomUUID()),
            productCode = path("productCode").asText(),
            categoryCode = path("categoryCode").asText(),
            status = path("status").asText(),
            defaultLocale = path("defaultLocale").asText(),
            contentTitle = path("contentTitle").takeIf { !it.isMissingNode && !it.isNull }?.asText(),
            contentDescription = path("contentDescription").takeIf { !it.isMissingNode && !it.isNull }?.asText(),
            mediaCount = path("mediaCount").asInt(0),
            variantCount = path("variantCount").asInt(0),
            searchableText = path("searchableText").asText(""),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
