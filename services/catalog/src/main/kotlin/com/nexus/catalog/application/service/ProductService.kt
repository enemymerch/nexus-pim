package com.nexus.catalog.application.service

import com.nexus.catalog.domain.model.Product
import com.nexus.catalog.domain.model.VariantOption
import com.nexus.catalog.domain.port.ProductStorePort
import com.nexus.catalog.infrastructure.client.AttributeServiceClient
import com.nexus.catalog.infrastructure.client.TaxonomyServiceClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productStore: ProductStorePort,
    private val taxonomyClient: TaxonomyServiceClient,
    private val attributeClient: AttributeServiceClient,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    fun createProduct(
        code: String,
        name: String,
        categoryCode: String,
        variantAttributeCodes: Set<String>
    ): ProductDetails {
        val normalizedCode = Product.normalizeCode(code)
        Product.validateCode(normalizedCode)

        if (productStore.existsByCode(normalizedCode)) {
            throw IllegalArgumentException("Product already exists: $normalizedCode")
        }

        val metadata = taxonomyClient.getCategoryMetadata(categoryCode)
        val normalizedVariantCodes = variantAttributeCodes
            .map(Product::normalizeAttributeCode)
            .onEach(Product::validateAttributeCode)
            .toSet()

        validateVariantAttributesWithTaxonomyAndAttribute(
            metadata.effectiveAttributeSetCodes,
            normalizedVariantCodes
        )

        val product = Product.create(
            code = normalizedCode,
            name = name,
            categoryCode = metadata.code,
            variantAttributeCodes = normalizedVariantCodes
        )

        return toDetails(productStore.save(product))
    }

    @Transactional
    fun createVariant(
        productCode: String,
        variantCode: String,
        active: Boolean,
        barcode: String?,
        options: Set<VariantOption>
    ): ProductDetails {
        val product = requireProduct(productCode)
        val metadata = taxonomyClient.getCategoryMetadata(product.categoryCode)
        val allowedValuesByAttribute = validateVariantAttributesWithTaxonomyAndAttribute(
            metadata.effectiveAttributeSetCodes,
            product.variantAttributeCodes()
        )
        validateVariantOptions(product, options, allowedValuesByAttribute)

        product.addVariant(
            variantCode = variantCode,
            active = active,
            barcode = barcode,
            options = options
        )

        return toDetails(productStore.save(product))
    }

    @Transactional(readOnly = true)
    fun getProduct(productCode: String): ProductDetails =
        toDetails(requireProduct(productCode))

    private fun requireProduct(code: String): Product {
        val normalizedCode = Product.normalizeCode(code)
        return productStore.findByCode(normalizedCode)
            ?: throw NoSuchElementException("Product not found: $normalizedCode")
    }

    private fun validateVariantAttributesWithTaxonomyAndAttribute(
        effectiveSetCodes: List<String>,
        variantAttributeCodes: Set<String>
    ): Map<String, Set<String>> {
        if (effectiveSetCodes.isEmpty()) {
            throw IllegalArgumentException("Category has no effective attribute sets.")
        }

        val allowedValuesByAttribute = mutableMapOf<String, MutableSet<String>>()
        val allowedAttributeCodes = mutableSetOf<String>()

        effectiveSetCodes.forEach { setCode ->
            val set = attributeClient.getAttributeSet(setCode)
            set.items.forEach { item ->
                val attrCode = Product.normalizeAttributeCode(item.attributeCode)
                allowedAttributeCodes += attrCode

                parseAllowedValues(item.constraintsJson).forEach { allowed ->
                    allowedValuesByAttribute.getOrPut(attrCode) { mutableSetOf() } +=
                        Product.normalizeOptionValueCode(allowed)
                }
            }
        }

        val notAllowed = variantAttributeCodes.filterNot { it in allowedAttributeCodes }
        if (notAllowed.isNotEmpty()) {
            throw IllegalArgumentException(
                "Variant attributes are not allowed by category attribute sets: ${notAllowed.sorted()}"
            )
        }

        variantAttributeCodes.forEach { attrCode ->
            val attr = attributeClient.getAttribute(attrCode)
            if (attr.scope != "VARIANT") {
                throw IllegalArgumentException("Attribute '$attrCode' must have scope VARIANT.")
            }
            if (attr.dataType != "ENUM") {
                throw IllegalArgumentException("Attribute '$attrCode' must have dataType ENUM for variant options.")
            }
        }

        return allowedValuesByAttribute.mapValues { it.value.toSet() }
    }

    private fun validateVariantOptions(
        product: Product,
        options: Set<VariantOption>,
        allowedValuesByAttribute: Map<String, Set<String>>
    ) {
        if (options.isEmpty()) {
            throw IllegalArgumentException("Variant options cannot be empty.")
        }

        val optionCodes = options.map { Product.normalizeAttributeCode(it.attributeCode) }.toSet()
        if (optionCodes != product.variantAttributeCodes()) {
            throw IllegalArgumentException("Variant options must match product variant attribute set exactly.")
        }

        optionCodes.forEach { attrCode ->
            val attr = attributeClient.getAttribute(attrCode)
            if (attr.scope != "VARIANT") {
                throw IllegalArgumentException("Attribute '$attrCode' must have scope VARIANT.")
            }
            if (attr.dataType != "ENUM") {
                throw IllegalArgumentException("Attribute '$attrCode' must have dataType ENUM for variant options.")
            }
        }

        validateVariantOptionsAgainstAllowedValues(options, allowedValuesByAttribute)
    }

    private fun validateVariantOptionsAgainstAllowedValues(
        options: Set<VariantOption>,
        allowedValuesByAttribute: Map<String, Set<String>>
    ) {
        options.forEach { option ->
            val attrCode = Product.normalizeAttributeCode(option.attributeCode)
            val allowed = allowedValuesByAttribute[attrCode].orEmpty()
            if (allowed.isEmpty()) {
                // No allowed list defined in constraints; keep behavior permissive.
                return@forEach
            }
            val normalizedValue = Product.normalizeOptionValueCode(option.attributeValueCode)
            if (normalizedValue !in allowed) {
                throw IllegalArgumentException(
                    "Variant option value '$normalizedValue' is not allowed for attribute '$attrCode'. Allowed: ${allowed.sorted()}"
                )
            }
        }
    }

    private fun parseAllowedValues(constraintsJson: String?): Set<String> {
        if (constraintsJson.isNullOrBlank()) return emptySet()
        return try {
            val node = objectMapper.readTree(constraintsJson)
            val allowedNode = node.get("allowed") ?: return emptySet()
            if (!allowedNode.isArray) return emptySet()
            allowedNode.mapNotNull { it.asText(null) }.toSet()
        } catch (_: Exception) {
            emptySet()
        }
    }

    private fun toDetails(product: Product): ProductDetails =
        ProductDetails(
            code = product.code,
            name = product.name,
            categoryCode = product.categoryCode,
            status = product.status.name,
            variantAttributeCodes = product.variantAttributeCodes().toList().sorted(),
            variants = product.variants().map { variant ->
                VariantDetails(
                    code = variant.code,
                    signature = variant.signature,
                    active = variant.active,
                    barcode = variant.barcode,
                    options = variant.options().map { option ->
                        VariantOptionDetails(
                            attributeCode = option.attributeCode,
                            attributeValueCode = option.attributeValueCode
                        )
                    }.sortedBy { it.attributeCode }
                )
            }.sortedBy { it.code }
        )
}

data class ProductDetails(
    val code: String,
    val name: String,
    val categoryCode: String,
    val status: String,
    val variantAttributeCodes: List<String>,
    val variants: List<VariantDetails>
)

data class VariantDetails(
    val code: String,
    val signature: String,
    val active: Boolean,
    val barcode: String?,
    val options: List<VariantOptionDetails>
)

data class VariantOptionDetails(
    val attributeCode: String,
    val attributeValueCode: String
)
