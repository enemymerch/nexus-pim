package com.nexus.search.web.dto

import com.nexus.search.application.service.SearchDocumentDetails
import java.time.Instant

data class SearchDocumentResponse(
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

fun SearchDocumentDetails.toResponse(): SearchDocumentResponse =
    SearchDocumentResponse(
        productCode = productCode,
        categoryCode = categoryCode,
        status = status,
        defaultLocale = defaultLocale,
        contentTitle = contentTitle,
        contentDescription = contentDescription,
        mediaCount = mediaCount,
        variantCount = variantCount,
        updatedAt = updatedAt
    )
