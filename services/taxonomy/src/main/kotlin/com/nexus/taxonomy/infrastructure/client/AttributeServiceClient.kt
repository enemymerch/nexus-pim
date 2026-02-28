package com.nexus.taxonomy.infrastructure.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class AttributeServiceClient(
    @Value("\${integration.attribute.base-url:http://localhost:8082}") baseUrl: String,
    builder: RestClient.Builder
) {
    private val client = builder.baseUrl(baseUrl).build()

    fun requireAttributeSetExists(setCode: String) {
        try {
            client.get()
                .uri("/api/attribute/attribute-sets/{setCode}", setCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException("Attribute set not found: $setCode (status=${response.statusCode.value()})")
                }
                .body(AttributeSetContract::class.java)
                ?: throw IllegalStateException("Empty response while checking attribute set: $setCode")
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: RestClientException) {
            throw IllegalStateException("Attribute service call failed for set '$setCode': ${ex.message}", ex)
        }
    }
}

data class AttributeSetContract(
    val code: String,
    val name: String
)
