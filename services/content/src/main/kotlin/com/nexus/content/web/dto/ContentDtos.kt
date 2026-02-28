package com.nexus.content.web.dto

import com.nexus.content.application.service.LocalizedContentDetails
import com.nexus.content.application.service.ProductContentDetails
import com.nexus.content.application.service.ResolvedContentDetails
import jakarta.validation.constraints.NotBlank

data class InitializeProductContentRequest(
    @field:NotBlank val defaultLocale: String,
    @field:NotBlank val title: String,
    val shortDescription: String? = null,
    val description: String? = null,
    val bullets: List<String> = emptyList()
)

data class UpsertContentRequest(
    @field:NotBlank val title: String,
    val shortDescription: String? = null,
    val description: String? = null,
    val bullets: List<String> = emptyList()
)

data class ProductContentResponse(
    val productCode: String,
    val defaultLocale: String,
    val locales: List<String>,
    val entries: List<LocalizedContentResponse>
)

data class LocalizedContentResponse(
    val locale: String,
    val channel: String?,
    val title: String,
    val shortDescription: String?,
    val description: String?,
    val bullets: List<String>
)

data class ResolvedContentResponse(
    val productCode: String,
    val defaultLocale: String,
    val locale: String,
    val channel: String?,
    val title: String,
    val shortDescription: String?,
    val description: String?,
    val bullets: List<String>
)

fun ProductContentDetails.toResponse(): ProductContentResponse =
    ProductContentResponse(
        productCode = productCode,
        defaultLocale = defaultLocale,
        locales = locales,
        entries = entries.map(LocalizedContentDetails::toResponse)
    )

fun LocalizedContentDetails.toResponse(): LocalizedContentResponse =
    LocalizedContentResponse(
        locale = locale,
        channel = channel,
        title = title,
        shortDescription = shortDescription,
        description = description,
        bullets = bullets
    )

fun ResolvedContentDetails.toResponse(): ResolvedContentResponse =
    ResolvedContentResponse(
        productCode = productCode,
        defaultLocale = defaultLocale,
        locale = locale,
        channel = channel,
        title = title,
        shortDescription = shortDescription,
        description = description,
        bullets = bullets
    )
