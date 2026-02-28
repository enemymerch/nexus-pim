package com.nexus.content.domain.model

import com.nexus.content.domain.valueobject.ContentEntryId
import com.nexus.content.domain.valueobject.ProductContentId
import java.time.Instant
import java.util.UUID

class ProductContent internal constructor(
    val id: ProductContentId,
    val productCode: String,
    var defaultLocale: String,
    private val entries: MutableList<LocalizedContent>,
    val createdAt: Instant,
    var updatedAt: Instant
) {

    fun entries(): List<LocalizedContent> = entries.sortedWith(compareBy<LocalizedContent> { it.locale }.thenBy { it.channel ?: "" })

    fun upsertLocaleContent(
        locale: String,
        title: String,
        shortDescription: String?,
        description: String?,
        bullets: List<String>,
        now: Instant = Instant.now()
    ): LocalizedContent = upsert(locale, null, title, shortDescription, description, bullets, now)

    fun upsertChannelOverride(
        locale: String,
        channel: String,
        title: String,
        shortDescription: String?,
        description: String?,
        bullets: List<String>,
        now: Instant = Instant.now()
    ): LocalizedContent = upsert(locale, channel, title, shortDescription, description, bullets, now)

    fun resolve(locale: String, channel: String?): LocalizedContent {
        val normalizedLocale = normalizeLocale(locale)
        val normalizedChannel = channel?.let(::normalizeCode)

        val exact = entries.firstOrNull { it.locale == normalizedLocale && it.channel == normalizedChannel }
        if (exact != null) return exact

        val localeBase = entries.firstOrNull { it.locale == normalizedLocale && it.channel == null }
        if (localeBase != null) return localeBase

        val defaultWithChannel = entries.firstOrNull { it.locale == defaultLocale && it.channel == normalizedChannel }
        if (defaultWithChannel != null) return defaultWithChannel

        return entries.firstOrNull { it.locale == defaultLocale && it.channel == null }
            ?: throw IllegalStateException("Default locale content is missing for product '$productCode'.")
    }

    fun locales(): Set<String> = entries.map { it.locale }.toSet()

    private fun upsert(
        locale: String,
        channel: String?,
        title: String,
        shortDescription: String?,
        description: String?,
        bullets: List<String>,
        now: Instant
    ): LocalizedContent {
        val normalizedLocale = normalizeLocale(locale)
        val normalizedChannel = channel?.let(::normalizeCode)
        validateContent(title, bullets)

        val existing = entries.firstOrNull { it.locale == normalizedLocale && it.channel == normalizedChannel }
        if (existing != null) {
            existing.title = title.trim()
            existing.shortDescription = shortDescription?.trim()?.takeIf { it.isNotBlank() }
            existing.description = description?.trim()?.takeIf { it.isNotBlank() }
            existing.bullets = bullets.map(String::trim).filter { it.isNotBlank() }
            existing.updatedAt = now
            updatedAt = now
            return existing
        }

        val created = LocalizedContent(
            id = ContentEntryId(UUID.randomUUID()),
            locale = normalizedLocale,
            channel = normalizedChannel,
            title = title.trim(),
            shortDescription = shortDescription?.trim()?.takeIf { it.isNotBlank() },
            description = description?.trim()?.takeIf { it.isNotBlank() },
            bullets = bullets.map(String::trim).filter { it.isNotBlank() },
            createdAt = now,
            updatedAt = now
        )
        entries += created
        updatedAt = now
        return created
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")
        private val LOCALE_REGEX = Regex("^[a-z]{2,3}(-[a-z0-9]{2,8})*$")

        fun create(
            productCode: String,
            defaultLocale: String,
            title: String,
            shortDescription: String?,
            description: String?,
            bullets: List<String>,
            now: Instant = Instant.now()
        ): ProductContent {
            val normalizedProductCode = normalizeCode(productCode)
            val normalizedDefaultLocale = normalizeLocale(defaultLocale)
            validateContent(title, bullets)

            val aggregate = ProductContent(
                id = ProductContentId(UUID.randomUUID()),
                productCode = normalizedProductCode,
                defaultLocale = normalizedDefaultLocale,
                entries = mutableListOf(),
                createdAt = now,
                updatedAt = now
            )
            aggregate.upsertLocaleContent(
                locale = normalizedDefaultLocale,
                title = title,
                shortDescription = shortDescription,
                description = description,
                bullets = bullets,
                now = now
            )
            return aggregate
        }

        fun normalizeCode(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(CODE_REGEX.matches(normalized)) {
                "Invalid code '$normalized'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
            return normalized
        }

        fun normalizeLocale(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(LOCALE_REGEX.matches(normalized)) { "Invalid locale '$normalized'." }
            return normalized
        }

        private fun validateContent(title: String, bullets: List<String>) {
            require(title.isNotBlank()) { "Title cannot be blank." }
            require(title.trim().length <= 255) { "Title length must be <= 255." }
            require(bullets.size <= 20) { "Bullets count must be <= 20." }
            require(bullets.none { it.trim().length > 200 }) { "Each bullet length must be <= 200." }
        }
    }
}

data class LocalizedContent(
    val id: ContentEntryId,
    val locale: String,
    val channel: String?,
    var title: String,
    var shortDescription: String?,
    var description: String?,
    var bullets: List<String>,
    val createdAt: Instant,
    var updatedAt: Instant
)
