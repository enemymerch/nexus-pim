package com.nexus.catalog.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/api/catalog/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "catalog", "status" to "ok")
}
