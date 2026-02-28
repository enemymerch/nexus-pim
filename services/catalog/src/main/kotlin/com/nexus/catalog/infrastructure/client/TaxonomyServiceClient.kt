package com.nexus.catalog.infrastructure.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class TaxonomyServiceClient(
    @Value("\${integration.taxonomy.base-url:http://localhost:8083}") baseUrl: String,
    builder: RestClient.Builder
) {
    private val client = builder.baseUrl(baseUrl).build()

    fun getCategoryMetadata(categoryCode: String): CategoryMetadataContract {
        val normalizedCode = categoryCode.trim().lowercase()
        return try {
            client.get()
                .uri("/api/taxonomy/categories/{categoryCode}/metadata", normalizedCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException("Category not found: $normalizedCode (status=${response.statusCode.value()})")
                }
                .body(CategoryMetadataContract::class.java)
                ?: throw IllegalStateException("Empty response for category metadata: $normalizedCode")
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: RestClientException) {
            throw IllegalStateException("Taxonomy service call failed for category '$normalizedCode': ${ex.message}", ex)
        }
    }
}

data class CategoryMetadataContract(
    val code: String,
    val name: String,
    val parentCode: String?,
    val depth: Int,
    val path: String,
    val effectiveAttributeSetCodes: List<String>
)
