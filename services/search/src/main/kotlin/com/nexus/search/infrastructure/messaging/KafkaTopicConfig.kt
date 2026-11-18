package com.nexus.search.infrastructure.messaging

import com.nexus.events.Topics
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {
    @Bean
    fun productSearchIndexRequestedTopic(): NewTopic =
        TopicBuilder.name(Topics.PRODUCT_SEARCH_INDEX_REQUESTED)
            .partitions(3)
            .replicas(3)
            .build()
}
