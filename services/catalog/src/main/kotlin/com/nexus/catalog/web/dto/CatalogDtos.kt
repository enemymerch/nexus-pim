package com.nexus.catalog.web.dto

import com.nexus.catalog.application.service.ProductDetails
import com.nexus.catalog.application.service.VariantDetails
import com.nexus.catalog.application.service.VariantOptionDetails
import com.nexus.catalog.domain.model.VariantOption
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateProductRequest(
    @field:NotBlank val code: String,
    @field:NotBlank val name: String,
    @field:NotBlank val categoryCode: String,
    @field:NotEmpty val variantAttributeCodes: Set<@NotBlank String>
)

data class CreateVariantRequest(
    @field:NotBlank val code: String,
    val active: Boolean = true,
    val barcode: String? = null,
    @field:NotEmpty @field:Valid val options: Set<CreateVariantOptionRequest>
)

data class CreateVariantOptionRequest(
    @field:NotBlank val attributeCode: String,
    @field:NotBlank val attributeValueCode: String
)

data class ProductResponse(
    val code: String,
    val name: String,
    val categoryCode: String,
    val status: String,
    val variantAttributeCodes: List<String>,
    val variants: List<VariantResponse>
)

data class VariantResponse(
    val code: String,
    val signature: String,
    val active: Boolean,
    val barcode: String?,
    val options: List<VariantOptionResponse>
)

data class VariantOptionResponse(
    val attributeCode: String,
    val attributeValueCode: String
)

fun CreateVariantRequest.toDomainOptions(): Set<VariantOption> =
    options.map {
        VariantOption(
            attributeCode = it.attributeCode,
            attributeValueCode = it.attributeValueCode
        )
    }.toSet()

fun ProductDetails.toResponse(): ProductResponse =
    ProductResponse(
        code = code,
        name = name,
        categoryCode = categoryCode,
        status = status,
        variantAttributeCodes = variantAttributeCodes,
        variants = variants.map(VariantDetails::toResponse)
    )

fun VariantDetails.toResponse(): VariantResponse =
    VariantResponse(
        code = code,
        signature = signature,
        active = active,
        barcode = barcode,
        options = options.map(VariantOptionDetails::toResponse)
    )

fun VariantOptionDetails.toResponse(): VariantOptionResponse =
    VariantOptionResponse(
        attributeCode = attributeCode,
        attributeValueCode = attributeValueCode
    )
