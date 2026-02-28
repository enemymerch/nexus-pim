package com.nexus.search.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/api/search/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "search", "status" to "ok")
}
