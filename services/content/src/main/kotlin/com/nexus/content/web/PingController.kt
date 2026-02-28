package com.nexus.content.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/api/content/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "content", "status" to "ok")
}
