package com.nexus.attribute.web.controller

import com.nexus.attribute.application.service.AttributeSetService
import com.nexus.attribute.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/attribute/attribute-sets")
class AttributeSetController(
    private val service: AttributeSetService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateAttributeSetRequest): AttributeSetResponse =
        service.createSet(req.code, req.name).toResponse()

    @PostMapping("/{setCode}/groups")
    @ResponseStatus(HttpStatus.CREATED)
    fun addGroup(@PathVariable setCode: String, @Valid @RequestBody req: CreateGroupRequest): AttributeSetResponse =
        service.addGroup(setCode, req.code, req.name, req.sortOrder).toResponse()

    @PutMapping("/{setCode}/items")
    @ResponseStatus(HttpStatus.OK)
    fun attach(@PathVariable setCode: String, @Valid @RequestBody req: AttachAttributeRequest): AttributeSetResponse =
        service.attachAttribute(
            setCode = setCode,
            attributeCode = req.attributeCode,
            required = req.required,
            sortOrder = req.sortOrder,
            groupCode = req.groupCode,
            constraintsJson = req.constraintsJson
        ).toResponse()

    @GetMapping("/{setCode}")
    fun get(@PathVariable setCode: String): AttributeSetResponse =
        service.get(setCode).toResponse()
}
