package com.nexus.search.infrastructure.messaging

import com.nexus.events.ProductSearchIndexRequestedEvent
import com.nexus.events.Topics
import com.nexus.search.application.service.SearchService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ProductSearchIndexRequestedListener(
    private val searchService: SearchService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = [Topics.PRODUCT_SEARCH_INDEX_REQUESTED],
        groupId = "\${spring.kafka.consumer.group-id:search-indexer}"
    )
    fun onEvent(event: ProductSearchIndexRequestedEvent) {
        logger.info(
            "Received ProductSearchIndexRequestedEvent eventId={} source={} reason={} productCode={}",
            event.eventId,
            event.sourceService,
            event.reason,
            event.productCode
        )

        searchService.reindexProduct(
            productCode = event.productCode,
            locale = event.locale ?: "en-us",
            channel = event.channel
        )
    }
}
