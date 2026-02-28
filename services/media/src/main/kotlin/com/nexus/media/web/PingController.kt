package com.nexus.media.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/api/media/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "media", "status" to "ok")
}
