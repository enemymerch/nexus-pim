package com.nexus.search.domain.model

import com.nexus.search.domain.valueobject.SearchDocumentId
import java.time.Instant
import java.util.UUID

class SearchDocument internal constructor(
    val id: SearchDocumentId,
    val productCode: String,
    var categoryCode: String,
    var status: String,
    var defaultLocale: String,
    var contentTitle: String?,
    var contentDescription: String?,
    var mediaCount: Int,
    var variantCount: Int,
    var searchableText: String,
    val createdAt: Instant,
    var updatedAt: Instant
) {

    fun refresh(
        categoryCode: String,
        status: String,
        defaultLocale: String,
        contentTitle: String?,
        contentDescription: String?,
        mediaCount: Int,
        variantCount: Int,
        now: Instant = Instant.now()
    ) {
        this.categoryCode = normalizeCode(categoryCode)
        this.status = normalizeStatus(status)
        this.defaultLocale = normalizeLocale(defaultLocale)
        this.contentTitle = contentTitle?.trim()?.takeIf { it.isNotBlank() }
        this.contentDescription = contentDescription?.trim()?.takeIf { it.isNotBlank() }
        this.mediaCount = mediaCount.coerceAtLeast(0)
        this.variantCount = variantCount.coerceAtLeast(0)
        this.searchableText = buildSearchableText(
            productCode = productCode,
            categoryCode = this.categoryCode,
            status = this.status,
            title = this.contentTitle,
            description = this.contentDescription
        )
        this.updatedAt = now
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")
        private val LOCALE_REGEX = Regex("^[a-z]{2,3}(-[a-z0-9]{2,8})*$")

        fun create(
            productCode: String,
            categoryCode: String,
            status: String,
            defaultLocale: String,
            contentTitle: String?,
            contentDescription: String?,
            mediaCount: Int,
            variantCount: Int,
            now: Instant = Instant.now()
        ): SearchDocument {
            val normalizedProductCode = normalizeCode(productCode)
            val normalizedCategoryCode = normalizeCode(categoryCode)
            val normalizedStatus = normalizeStatus(status)
            val normalizedLocale = normalizeLocale(defaultLocale)
            val normalizedTitle = contentTitle?.trim()?.takeIf { it.isNotBlank() }
            val normalizedDescription = contentDescription?.trim()?.takeIf { it.isNotBlank() }
            return SearchDocument(
                id = SearchDocumentId(UUID.randomUUID()),
                productCode = normalizedProductCode,
                categoryCode = normalizedCategoryCode,
                status = normalizedStatus,
                defaultLocale = normalizedLocale,
                contentTitle = normalizedTitle,
                contentDescription = normalizedDescription,
                mediaCount = mediaCount.coerceAtLeast(0),
                variantCount = variantCount.coerceAtLeast(0),
                searchableText = buildSearchableText(
                    productCode = normalizedProductCode,
                    categoryCode = normalizedCategoryCode,
                    status = normalizedStatus,
                    title = normalizedTitle,
                    description = normalizedDescription
                ),
                createdAt = now,
                updatedAt = now
            )
        }

        fun normalizeCode(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(CODE_REGEX.matches(normalized)) { "Invalid code '$normalized'." }
            return normalized
        }

        fun normalizeLocale(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(LOCALE_REGEX.matches(normalized)) { "Invalid locale '$normalized'." }
            return normalized
        }

        private fun normalizeStatus(raw: String): String {
            val normalized = raw.trim().uppercase()
            require(normalized.isNotBlank()) { "status cannot be blank." }
            return normalized
        }

        private fun buildSearchableText(
            productCode: String,
            categoryCode: String,
            status: String,
            title: String?,
            description: String?
        ): String = listOfNotNull(productCode, categoryCode, status, title, description)
            .joinToString(" ")
            .lowercase()
    }
}
