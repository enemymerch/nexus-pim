package com.nexus.attribute.web.dto

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.enums.AttributeScope
import com.nexus.attribute.domain.model.Attribute
import jakarta.validation.constraints.NotBlank

data class CreateAttributeRequest(
    @field:NotBlank val code: String,
    val dataType: AttributeDataType,
    val scope: AttributeScope,
    val localizable: Boolean = false,
    val channelOverridable: Boolean = false,
    val multiValue: Boolean = false
)

data class AttributeResponse(
    val code: String,
    val dataType: AttributeDataType,
    val scope: AttributeScope,
    val localizable: Boolean,
    val channelOverridable: Boolean,
    val multiValue: Boolean
)

fun Attribute.toResponse(): AttributeResponse =
    AttributeResponse(
        code = code,
        dataType = dataType,
        scope = scope,
        localizable = localizable,
        channelOverridable = channelOverridable,
        multiValue = multiValue
    )
