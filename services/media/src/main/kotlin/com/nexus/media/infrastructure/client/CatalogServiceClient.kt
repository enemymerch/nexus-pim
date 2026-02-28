package com.nexus.media.infrastructure.client

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

    fun requireProductExists(productCode: String, variantCode: String?) {
        val normalizedProductCode = productCode.trim().lowercase()
        val product = try {
            client.get()
                .uri("/api/catalog/products/{productCode}", normalizedProductCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException(
                        "Product not found in catalog: $normalizedProductCode (status=${response.statusCode.value()})"
                    )
                }
                .body(CatalogProductContract::class.java)
                ?: throw IllegalStateException("Empty response for product '$normalizedProductCode' from catalog service.")
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: RestClientException) {
            throw IllegalStateException("Catalog service call failed for '$normalizedProductCode': ${ex.message}", ex)
        }

        if (variantCode != null) {
            val normalizedVariantCode = variantCode.trim().lowercase()
            val exists = product.variants.any { it.code == normalizedVariantCode }
            require(exists) { "Variant '$normalizedVariantCode' not found for product '$normalizedProductCode'." }
        }
    }
}

data class CatalogProductContract(
    val code: String,
    val variants: List<CatalogVariantContract> = emptyList()
)

data class CatalogVariantContract(
    val code: String
)
