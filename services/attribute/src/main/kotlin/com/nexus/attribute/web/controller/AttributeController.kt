package com.nexus.attribute.web.controller

import com.nexus.attribute.application.service.AttributeService
import com.nexus.attribute.domain.enums.AttributeScope
import com.nexus.attribute.web.dto.CreateAttributeRequest
import com.nexus.attribute.web.dto.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/attribute/attributes")
class AttributeController(
    private val service: AttributeService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateAttributeRequest) =
        service.create(
            code = req.code,
            dataType = req.dataType,
            scope = req.scope,
            localizable = req.localizable,
            channelOverridable = req.channelOverridable,
            multiValue = req.multiValue
        ).toResponse()

    @GetMapping("/{code}")
    fun get(@PathVariable code: String) =
        service.getByCode(code).toResponse()

    @GetMapping
    fun list(@RequestParam(required = false) scope: AttributeScope?) =
        service.list(scope).map { it.toResponse() }
}
