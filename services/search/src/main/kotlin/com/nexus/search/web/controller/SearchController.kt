package com.nexus.search.web.controller

import com.nexus.search.application.service.SearchService
import com.nexus.search.web.dto.toResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/search/products")
class SearchController(
    private val searchService: SearchService
) {

    @PostMapping("/{productCode}/reindex")
    fun reindex(
        @PathVariable productCode: String,
        @RequestParam(defaultValue = "en-us") locale: String,
        @RequestParam(required = false) channel: String?
    ) = searchService.reindexProduct(productCode = productCode, locale = locale, channel = channel).toResponse()

    @GetMapping("/{productCode}")
    fun getDocument(@PathVariable productCode: String) =
        searchService.getDocument(productCode).toResponse()

    @GetMapping
    fun search(
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) categoryCode: String?,
        @RequestParam(required = false) status: String?,
        @RequestParam(defaultValue = "20") limit: Int
    ) = searchService.search(q, categoryCode, status, limit).map { it.toResponse() }
}
