package com.nexus.catalog.infrastructure.client

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

    fun getAttribute(code: String): AttributeContract {
        val normalizedCode = code.trim().lowercase()
        return try {
            client.get()
                .uri("/api/attribute/attributes/{code}", normalizedCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException("Attribute not found: $normalizedCode (status=${response.statusCode.value()})")
                }
                .body(AttributeContract::class.java)
                ?: throw IllegalStateException("Empty response for attribute: $normalizedCode")
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: RestClientException) {
            throw IllegalStateException("Attribute service call failed for '$normalizedCode': ${ex.message}", ex)
        }
    }

    fun getAttributeSet(setCode: String): AttributeSetContract {
        val normalizedCode = setCode.trim().lowercase()
        return try {
            client.get()
                .uri("/api/attribute/attribute-sets/{setCode}", normalizedCode)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError) { _, response ->
                    throw IllegalArgumentException("Attribute set not found: $normalizedCode (status=${response.statusCode.value()})")
                }
                .body(AttributeSetContract::class.java)
                ?: throw IllegalStateException("Empty response for attribute set: $normalizedCode")
        } catch (ex: IllegalArgumentException) {
            throw ex
        } catch (ex: RestClientException) {
            throw IllegalStateException("Attribute service call failed for set '$normalizedCode': ${ex.message}", ex)
        }
    }
}

data class AttributeContract(
    val code: String,
    val dataType: String,
    val scope: String,
    val localizable: Boolean,
    val channelOverridable: Boolean,
    val multiValue: Boolean
)

data class AttributeSetContract(
    val code: String,
    val name: String,
    val items: List<AttributeSetItemContract>
) {
    val itemAttributeCodes: List<String>
        get() = items.map { it.attributeCode }
}

data class AttributeSetItemContract(
    val attributeCode: String,
    val required: Boolean,
    val sortOrder: Int,
    val groupCode: String?,
    val constraintsJson: String?
)
