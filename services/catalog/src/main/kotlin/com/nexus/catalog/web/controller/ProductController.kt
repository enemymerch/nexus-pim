package com.nexus.catalog.web.controller

import com.nexus.catalog.application.service.ProductService
import com.nexus.catalog.web.dto.CreateProductRequest
import com.nexus.catalog.web.dto.CreateVariantRequest
import com.nexus.catalog.web.dto.toDomainOptions
import com.nexus.catalog.web.dto.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/catalog/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@Valid @RequestBody req: CreateProductRequest) =
        productService.createProduct(
            code = req.code,
            name = req.name,
            categoryCode = req.categoryCode,
            variantAttributeCodes = req.variantAttributeCodes
        ).toResponse()

    @PostMapping("/{productCode}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    fun createVariant(@PathVariable productCode: String, @Valid @RequestBody req: CreateVariantRequest) =
        productService.createVariant(
            productCode = productCode,
            variantCode = req.code,
            active = req.active,
            barcode = req.barcode,
            options = req.toDomainOptions()
        ).toResponse()

    @GetMapping("/{productCode}")
    fun getProduct(@PathVariable productCode: String) =
        productService.getProduct(productCode).toResponse()
}
