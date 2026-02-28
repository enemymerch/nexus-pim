package com.nexus.search.infrastructure.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class MediaServiceClient(
    @Value("\${integration.media.base-url:http://localhost:8085}") baseUrl: String,
    builder: RestClient.Builder
) {
    private val client = builder.baseUrl(baseUrl).build()

    fun listByProduct(productCode: String): List<MediaAssetContract> {
        val normalizedCode = productCode.trim().lowercase()
        return try {
            client.get()
                .uri("/api/media/assets?productCode={productCode}", normalizedCode)
                .retrieve()
                .body(Array<MediaAssetContract>::class.java)
                ?.toList()
                ?: emptyList()
        } catch (_: RestClientException) {
            emptyList()
        }
    }
}

data class MediaAssetContract(
    val id: String,
    val status: String
)
