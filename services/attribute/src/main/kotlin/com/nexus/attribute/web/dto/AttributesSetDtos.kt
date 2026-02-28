package com.nexus.attribute.web.dto

import jakarta.validation.constraints.NotBlank

data class CreateAttributeSetRequest(
    @field:NotBlank val code: String,
    @field:NotBlank val name: String
)

data class CreateGroupRequest(
    @field:NotBlank val code: String,
    @field:NotBlank val name: String,
    val sortOrder: Int
)

data class AttachAttributeRequest(
    @field:NotBlank val attributeCode: String,
    val required: Boolean,
    val sortOrder: Int,
    val groupCode: String? = null,
    val constraintsJson: String? = null
)
