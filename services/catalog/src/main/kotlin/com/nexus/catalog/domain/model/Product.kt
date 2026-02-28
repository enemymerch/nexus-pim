package com.nexus.catalog.domain.model

import com.nexus.catalog.domain.valueobject.ProductId
import com.nexus.catalog.domain.valueobject.VariantId
import java.time.Instant
import java.util.UUID

class Product internal constructor(
    val id: ProductId,
    val code: String,
    var name: String,
    val categoryCode: String,
    var status: ProductStatus,
    private val variantAttributeCodes: MutableSet<String>,
    private val variants: MutableSet<Variant>,
    val createdAt: Instant
) {

    fun variantAttributeCodes(): Set<String> = variantAttributeCodes.toSet()

    fun variants(): Set<Variant> = variants.toSet()

    fun rename(newName: String) {
        require(newName.isNotBlank()) { "Product name cannot be blank." }
        name = newName.trim()
    }

    fun setVariantAttributes(attributeCodes: Set<String>) {
        require(attributeCodes.isNotEmpty()) { "Variant attributes cannot be empty." }

        val normalized = attributeCodes.map(::normalizeAttributeCode).toSet()
        normalized.forEach(::validateAttributeCode)

        if (variants.isNotEmpty() && variantAttributeCodes != normalized) {
            throw IllegalStateException(
                "Cannot change variant attribute set after variant creation. Regenerate variants in a new version."
            )
        }

        variantAttributeCodes.clear()
        variantAttributeCodes.addAll(normalized)
    }

    fun addVariant(
        variantCode: String,
        active: Boolean,
        barcode: String?,
        options: Set<VariantOption>,
        now: Instant = Instant.now()
    ): Variant {
        val normalizedVariantCode = Variant.normalizeCode(variantCode)
        Variant.validateCode(normalizedVariantCode)
        require(options.isNotEmpty()) { "Variant options cannot be empty." }
        require(variantAttributeCodes.isNotEmpty()) { "Variant attributes must be defined before creating variant." }

        val normalizedOptions = options.map { option ->
            val attributeCode = normalizeAttributeCode(option.attributeCode)
            val valueCode = normalizeOptionValueCode(option.attributeValueCode)
            validateAttributeCode(attributeCode)
            validateOptionValueCode(valueCode)
            VariantOption(attributeCode = attributeCode, attributeValueCode = valueCode)
        }.toSet()

        val optionAttrCodes = normalizedOptions.map { it.attributeCode }
        require(optionAttrCodes.distinct().size == optionAttrCodes.size) {
            "Variant cannot contain duplicate attribute options."
        }
        require(optionAttrCodes.toSet() == variantAttributeCodes) {
            "Variant options must contain exactly product variant attributes."
        }

        val signature = buildSignature(normalizedOptions)

        require(variants.none { it.code == normalizedVariantCode }) {
            "Variant already exists with code: $normalizedVariantCode"
        }
        require(variants.none { it.signature == signature }) {
            "Variant already exists with same combination."
        }

        val variant = Variant(
            id = VariantId(UUID.randomUUID()),
            code = normalizedVariantCode,
            signature = signature,
            active = active,
            barcode = barcode?.trim()?.takeIf { it.isNotBlank() },
            options = normalizedOptions.toMutableSet(),
            createdAt = now
        )
        variants += variant
        return variant
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun create(
            code: String,
            name: String,
            categoryCode: String,
            variantAttributeCodes: Set<String>,
            now: Instant = Instant.now()
        ): Product {
            val normalizedCode = normalizeCode(code)
            validateCode(normalizedCode)
            require(name.isNotBlank()) { "Product name cannot be blank." }

            val normalizedCategoryCode = normalizeCode(categoryCode)
            validateCode(normalizedCategoryCode)

            val normalizedVariantAttributes = variantAttributeCodes.map(::normalizeAttributeCode).toSet()
            require(normalizedVariantAttributes.isNotEmpty()) { "Variant attributes cannot be empty." }
            normalizedVariantAttributes.forEach(::validateAttributeCode)

            return Product(
                id = ProductId(UUID.randomUUID()),
                code = normalizedCode,
                name = name.trim(),
                categoryCode = normalizedCategoryCode,
                status = ProductStatus.DRAFT,
                variantAttributeCodes = normalizedVariantAttributes.toMutableSet(),
                variants = mutableSetOf(),
                createdAt = now
            )
        }

        fun normalizeCode(raw: String): String = raw.trim().lowercase()

        fun validateCode(code: String) {
            require(code.isNotBlank()) { "Product code cannot be blank." }
            require(CODE_REGEX.matches(code)) {
                "Invalid product code '$code'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
        }

        fun normalizeAttributeCode(raw: String): String = normalizeCode(raw)

        fun validateAttributeCode(code: String) = validateCode(code)

        fun normalizeOptionValueCode(raw: String): String = normalizeCode(raw)

        fun validateOptionValueCode(code: String) {
            require(code.isNotBlank()) { "Variant option value cannot be blank." }
            require(code.length in 1..64) { "Variant option value length must be between 1 and 64." }
            require(code.all { it.isLowerCase() || it.isDigit() || it == '_' || it == '-' }) {
                "Invalid variant option value '$code'. Use 1-64 chars: lowercase letters, numbers, '_' or '-'."
            }
        }

        fun buildSignature(options: Set<VariantOption>): String =
            options.sortedBy { it.attributeCode }
                .joinToString("|") { "${it.attributeCode}:${it.attributeValueCode}" }
    }
}

enum class ProductStatus {
    DRAFT,
    ACTIVE,
    ARCHIVED
}
