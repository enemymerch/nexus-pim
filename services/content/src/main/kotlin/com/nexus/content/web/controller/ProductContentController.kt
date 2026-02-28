package com.nexus.content.web.controller

import com.nexus.content.application.service.ContentService
import com.nexus.content.web.dto.InitializeProductContentRequest
import com.nexus.content.web.dto.UpsertContentRequest
import com.nexus.content.web.dto.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/content/products")
class ProductContentController(
    private val contentService: ContentService
) {

    @PostMapping("/{productCode}")
    @ResponseStatus(HttpStatus.CREATED)
    fun initialize(
        @PathVariable productCode: String,
        @Valid @RequestBody req: InitializeProductContentRequest
    ) = contentService.initializeProductContent(
        productCode = productCode,
        defaultLocale = req.defaultLocale,
        title = req.title,
        shortDescription = req.shortDescription,
        description = req.description,
        bullets = req.bullets
    ).toResponse()

    @PutMapping("/{productCode}/locales/{locale}")
    fun upsertLocale(
        @PathVariable productCode: String,
        @PathVariable locale: String,
        @Valid @RequestBody req: UpsertContentRequest
    ) = contentService.upsertLocaleContent(
        productCode = productCode,
        locale = locale,
        title = req.title,
        shortDescription = req.shortDescription,
        description = req.description,
        bullets = req.bullets
    ).toResponse()

    @PutMapping("/{productCode}/locales/{locale}/channels/{channel}")
    fun upsertChannel(
        @PathVariable productCode: String,
        @PathVariable locale: String,
        @PathVariable channel: String,
        @Valid @RequestBody req: UpsertContentRequest
    ) = contentService.upsertChannelOverride(
        productCode = productCode,
        locale = locale,
        channel = channel,
        title = req.title,
        shortDescription = req.shortDescription,
        description = req.description,
        bullets = req.bullets
    ).toResponse()

    @GetMapping("/{productCode}")
    fun getProductContent(@PathVariable productCode: String) =
        contentService.getProductContent(productCode).toResponse()

    @GetMapping("/{productCode}/resolve")
    fun resolve(
        @PathVariable productCode: String,
        @RequestParam locale: String,
        @RequestParam(required = false) channel: String?
    ) = contentService.resolve(productCode, locale, channel).toResponse()
}
