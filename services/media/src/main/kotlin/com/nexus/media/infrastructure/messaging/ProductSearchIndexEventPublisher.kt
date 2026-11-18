package com.nexus.media.infrastructure.messaging

import com.nexus.events.ProductSearchIndexRequestedEvent
import com.nexus.events.Topics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductSearchIndexEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, ProductSearchIndexRequestedEvent>
) {
    fun publish(productCode: String, reason: String, locale: String? = null, channel: String? = null) {
        val event = ProductSearchIndexRequestedEvent(
            sourceService = "media",
            reason = reason,
            productCode = productCode.trim().lowercase(),
            locale = locale?.trim()?.lowercase(),
            channel = channel?.trim()?.lowercase()
        )
        kafkaTemplate.send(Topics.PRODUCT_SEARCH_INDEX_REQUESTED, event.productCode, event)
    }
}
