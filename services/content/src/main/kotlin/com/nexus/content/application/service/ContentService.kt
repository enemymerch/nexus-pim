package com.nexus.content.application.service

import com.nexus.content.domain.model.ProductContent
import com.nexus.content.domain.port.ProductContentStorePort
import com.nexus.content.infrastructure.client.CatalogServiceClient
import com.nexus.content.infrastructure.messaging.ProductSearchIndexEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContentService(
    private val store: ProductContentStorePort,
    private val catalogClient: CatalogServiceClient,
    private val indexEventPublisher: ProductSearchIndexEventPublisher
) {

    @Transactional
    fun initializeProductContent(
        productCode: String,
        defaultLocale: String,
        title: String,
        shortDescription: String?,
        description: String?,
        bullets: List<String>
    ): ProductContentDetails {
        val normalizedProductCode = ProductContent.normalizeCode(productCode)

        if (store.existsByProductCode(normalizedProductCode)) {
            throw IllegalArgumentException("Product content already exists: $normalizedProductCode")
        }

        catalogClient.requireProductExists(normalizedProductCode)

        val created = ProductContent.create(
            productCode = normalizedProductCode,
            defaultLocale = defaultLocale,
            title = title,
            shortDescription = shortDescription,
            description = description,
            bullets = bullets
        )

        val saved = store.save(created)
        indexEventPublisher.publish(saved.productCode, "CONTENT_INITIALIZED", saved.defaultLocale, null)
        return toDetails(saved)
    }

    @Transactional
    fun upsertLocaleContent(
        productCode: String,
        locale: String,
        title: String,
        shortDescription: String?,
        description: String?,
        bullets: List<String>
    ): ProductContentDetails {
        val aggregate = requireContent(productCode)
        aggregate.upsertLocaleContent(locale, title, shortDescription, description, bullets)
        val saved = store.save(aggregate)
        indexEventPublisher.publish(saved.productCode, "CONTENT_LOCALE_UPSERTED", locale, null)
        return toDetails(saved)
    }

    @Transactional
    fun upsertChannelOverride(
        productCode: String,
        locale: String,
        channel: String,
        title: String,
        shortDescription: String?,
        description: String?,
        bullets: List<String>
    ): ProductContentDetails {
        val aggregate = requireContent(productCode)
        aggregate.upsertChannelOverride(locale, channel, title, shortDescription, description, bullets)
        val saved = store.save(aggregate)
        indexEventPublisher.publish(saved.productCode, "CONTENT_CHANNEL_OVERRIDE_UPSERTED", locale, channel)
        return toDetails(saved)
    }

    @Transactional(readOnly = true)
    fun resolve(productCode: String, locale: String, channel: String?): ResolvedContentDetails {
        val aggregate = requireContent(productCode)
        val resolved = aggregate.resolve(locale, channel)

        return ResolvedContentDetails(
            productCode = aggregate.productCode,
            defaultLocale = aggregate.defaultLocale,
            locale = resolved.locale,
            channel = resolved.channel,
            title = resolved.title,
            shortDescription = resolved.shortDescription,
            description = resolved.description,
            bullets = resolved.bullets
        )
    }

    @Transactional(readOnly = true)
    fun getProductContent(productCode: String): ProductContentDetails =
        toDetails(requireContent(productCode))

    private fun requireContent(productCode: String): ProductContent {
        val normalizedProductCode = ProductContent.normalizeCode(productCode)
        return store.findByProductCode(normalizedProductCode)
            ?: throw NoSuchElementException("Product content not found: $normalizedProductCode")
    }

    private fun toDetails(content: ProductContent): ProductContentDetails =
        ProductContentDetails(
            productCode = content.productCode,
            defaultLocale = content.defaultLocale,
            locales = content.locales().toList().sorted(),
            entries = content.entries().map { entry ->
                LocalizedContentDetails(
                    locale = entry.locale,
                    channel = entry.channel,
                    title = entry.title,
                    shortDescription = entry.shortDescription,
                    description = entry.description,
                    bullets = entry.bullets
                )
            }
        )
}

data class ProductContentDetails(
    val productCode: String,
    val defaultLocale: String,
    val locales: List<String>,
    val entries: List<LocalizedContentDetails>
)

data class LocalizedContentDetails(
    val locale: String,
    val channel: String?,
    val title: String,
    val shortDescription: String?,
    val description: String?,
    val bullets: List<String>
)

data class ResolvedContentDetails(
    val productCode: String,
    val defaultLocale: String,
    val locale: String,
    val channel: String?,
    val title: String,
    val shortDescription: String?,
    val description: String?,
    val bullets: List<String>
)
