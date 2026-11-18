package com.nexus.events

import java.time.Instant
import java.util.UUID

data class ProductSearchIndexRequestedEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val occurredAt: Instant = Instant.now(),
    val sourceService: String,
    val reason: String,
    val productCode: String,
    val locale: String? = null,
    val channel: String? = null
)
