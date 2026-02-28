package com.nexus.attribute.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/api/attribute/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "attribute", "status" to "ok")
}
