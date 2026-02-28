package com.nexus.taxonomy.web.controller

import com.nexus.taxonomy.application.service.TaxonomyService
import com.nexus.taxonomy.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/taxonomy/categories")
class CategoryController(
    private val taxonomyService: TaxonomyService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateCategoryRequest): CategoryResponse =
        taxonomyService.createCategory(req.code, req.name, req.parentCode).toResponse()

    @PutMapping("/{categoryCode}")
    fun update(
        @PathVariable categoryCode: String,
        @Valid @RequestBody req: UpdateCategoryRequest
    ): CategoryResponse {
        if (req.name == null && !req.updateParent) {
            throw IllegalArgumentException("Nothing to update. Provide 'name' and/or set 'updateParent=true'.")
        }

        return taxonomyService.updateCategory(
            code = categoryCode,
            newName = req.name,
            newParentCode = req.parentCode,
            updateParent = req.updateParent
        ).toResponse()
    }

    @PutMapping("/{categoryCode}/attribute-sets")
    fun replaceAttributeSets(
        @PathVariable categoryCode: String,
        @Valid @RequestBody req: ReplaceCategoryAttributeSetsRequest
    ): CategoryResponse =
        taxonomyService.replaceCategoryAttributeSets(categoryCode, req.attributeSetCodes).toResponse()

    @GetMapping("/{categoryCode}")
    fun get(@PathVariable categoryCode: String): CategoryResponse =
        taxonomyService.getCategory(categoryCode).toResponse()

    @GetMapping("/{categoryCode}/effective-attribute-sets")
    fun resolveEffective(@PathVariable categoryCode: String): EffectiveAttributeSetsResponse =
        EffectiveAttributeSetsResponse(
            categoryCode = categoryCode.trim().lowercase(),
            attributeSetCodes = taxonomyService.resolveEffectiveAttributeSetCodes(categoryCode)
        )

    @GetMapping("/{categoryCode}/metadata")
    fun metadata(@PathVariable categoryCode: String): CategoryMetadataResponse =
        taxonomyService.getCategoryMetadata(categoryCode).toResponse()
}
