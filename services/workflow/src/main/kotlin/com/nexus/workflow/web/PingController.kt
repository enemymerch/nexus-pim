package com.nexus.workflow.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/api/workflow/ping")
    fun ping(): Map<String, String> =
        mapOf("service" to "workflow", "status" to "ok")
}
