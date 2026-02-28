package com.nexus.workflow.infrastructure.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class CatalogServiceClient(
    @Value("\${integration.catalog.base-url:http://localhost:8081}") baseUrl: String,
    builder: RestClient.Builder
) {
    private val client = builder.baseUrl(baseUrl).build()

    fun requireProductExists(productCode: String): CatalogProductContract {
        val normalizedCode = productCode.trim().lowercase()
        return try {
            client.get()
                .uri("/api/catalog/products/{productCode}", normalizedCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException("Product not found in catalog: $normalizedCode (status=${response.statusCode.value()})")
                }
                .body(CatalogProductContract::class.java)
                ?: throw IllegalStateException("Empty response for product '$normalizedCode' from catalog service.")
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: RestClientException) {
            throw IllegalStateException("Catalog service call failed for '$normalizedCode': ${ex.message}", ex)
        }
    }
}

data class CatalogProductContract(
    val code: String,
    val status: String
)
