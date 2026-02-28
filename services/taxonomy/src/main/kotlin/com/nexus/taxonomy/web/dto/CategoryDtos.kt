package com.nexus.taxonomy.web.dto

import com.nexus.taxonomy.application.service.CategoryDetails
import com.nexus.taxonomy.application.service.CategoryMetadata
import jakarta.validation.constraints.NotBlank

data class CreateCategoryRequest(
    @field:NotBlank val code: String,
    @field:NotBlank val name: String,
    val parentCode: String? = null
)

data class UpdateCategoryRequest(
    val name: String? = null,
    val parentCode: String? = null,
    val updateParent: Boolean = false
)

data class ReplaceCategoryAttributeSetsRequest(
    val attributeSetCodes: Set<@NotBlank String> = emptySet()
)

data class EffectiveAttributeSetsResponse(
    val categoryCode: String,
    val attributeSetCodes: List<String>
)

data class CategoryMetadataResponse(
    val code: String,
    val name: String,
    val parentCode: String?,
    val depth: Int,
    val path: String,
    val effectiveAttributeSetCodes: List<String>
)

data class CategoryResponse(
    val code: String,
    val name: String,
    val parentCode: String?,
    val depth: Int,
    val path: String,
    val directAttributeSetCodes: List<String>,
    val effectiveAttributeSetCodes: List<String>
)

fun CategoryDetails.toResponse(): CategoryResponse =
    CategoryResponse(
        code = code,
        name = name,
        parentCode = parentCode,
        depth = depth,
        path = path,
        directAttributeSetCodes = directAttributeSetCodes,
        effectiveAttributeSetCodes = effectiveAttributeSetCodes
    )

fun CategoryMetadata.toResponse(): CategoryMetadataResponse =
    CategoryMetadataResponse(
        code = code,
        name = name,
        parentCode = parentCode,
        depth = depth,
        path = path,
        effectiveAttributeSetCodes = effectiveAttributeSetCodes
    )
