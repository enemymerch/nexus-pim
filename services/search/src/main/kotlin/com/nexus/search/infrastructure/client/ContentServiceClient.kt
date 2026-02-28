package com.nexus.search.infrastructure.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class ContentServiceClient(
    @Value("\${integration.content.base-url:http://localhost:8084}") baseUrl: String,
    builder: RestClient.Builder
) {
    private val client = builder.baseUrl(baseUrl).build()

    fun resolve(productCode: String, locale: String, channel: String?): ResolvedContentContract? {
        val normalizedCode = productCode.trim().lowercase()
        return try {
            client.get()
                .uri { uriBuilder ->
                    val builder = uriBuilder
                        .path("/api/content/products/{productCode}/resolve")
                        .queryParam("locale", locale)
                    if (!channel.isNullOrBlank()) {
                        builder.queryParam("channel", channel)
                    }
                    builder.build(normalizedCode)
                }
                .retrieve()
                .body(ResolvedContentContract::class.java)
        } catch (_: RestClientException) {
            null
        }
    }
}

data class ResolvedContentContract(
    val defaultLocale: String,
    val title: String?,
    val description: String?
)
