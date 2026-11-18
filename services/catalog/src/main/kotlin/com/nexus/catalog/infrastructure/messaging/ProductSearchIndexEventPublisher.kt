package com.nexus.catalog.infrastructure.messaging

import com.nexus.events.ProductSearchIndexRequestedEvent
import com.nexus.events.Topics
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ProductSearchIndexEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, ProductSearchIndexRequestedEvent>
) {
    fun publish(productCode: String, reason: String) {
        val event = ProductSearchIndexRequestedEvent(
            sourceService = "catalog",
            reason = reason,
            productCode = productCode.trim().lowercase()
        )
        kafkaTemplate.send(Topics.PRODUCT_SEARCH_INDEX_REQUESTED, event.productCode, event)
    }
}
