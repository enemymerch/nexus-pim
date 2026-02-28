package com.nexus.catalog.infrastructure.persistence

import com.nexus.catalog.domain.model.Product
import com.nexus.catalog.domain.model.ProductStatus
import com.nexus.catalog.domain.model.Variant
import com.nexus.catalog.domain.model.VariantOption
import com.nexus.catalog.domain.valueobject.ProductId
import com.nexus.catalog.domain.valueobject.VariantId
import java.time.Instant

object ProductMapper {

    fun toJpa(product: Product): ProductJpaEntity =
        ProductJpaEntity(
            id = product.id.value,
            code = product.code,
            name = product.name,
            categoryCode = product.categoryCode,
            status = ProductStatusDb.valueOf(product.status.name),
            createdAt = product.createdAt
        )

    fun toDomain(
        product: ProductJpaEntity,
        variantAttributes: List<ProductVariantAttributeJpaEntity>,
        variants: List<VariantJpaEntity>,
        options: List<VariantOptionJpaEntity>
    ): Product {
        val optionsByVariantId = options.groupBy { it.variant.id }

        val domainVariants = variants.map { variant ->
            Variant(
                id = VariantId(variant.id),
                code = variant.code,
                signature = variant.signature,
                active = variant.active,
                barcode = variant.barcode,
                options = optionsByVariantId[variant.id].orEmpty().map { option ->
                    VariantOption(
                        attributeCode = option.attributeCode,
                        attributeValueCode = option.attributeValueCode
                    )
                }.toMutableSet(),
                createdAt = variant.createdAt
            )
        }.toMutableSet()

        return Product(
            id = ProductId(product.id),
            code = product.code,
            name = product.name,
            categoryCode = product.categoryCode,
            status = ProductStatus.valueOf(product.status.name),
            variantAttributeCodes = variantAttributes.map { it.attributeCode }.toMutableSet(),
            variants = domainVariants,
            createdAt = product.createdAt
        )
    }

    fun now(): Instant = Instant.now()
}
