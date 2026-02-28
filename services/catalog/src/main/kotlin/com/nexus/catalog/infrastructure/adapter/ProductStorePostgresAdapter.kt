package com.nexus.catalog.infrastructure.adapter

import com.nexus.catalog.domain.model.Product
import com.nexus.catalog.domain.port.ProductStorePort
import com.nexus.catalog.infrastructure.persistence.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class ProductStorePostgresAdapter(
    private val productRepo: ProductJpaRepository,
    private val variantAttributeRepo: ProductVariantAttributeJpaRepository,
    private val variantRepo: VariantJpaRepository,
    private val variantOptionRepo: VariantOptionJpaRepository
) : ProductStorePort {

    @Transactional
    override fun save(product: Product): Product {
        val existing = productRepo.findByCode(product.code)
        val persistedProduct = if (existing != null) {
            existing.apply {
                name = product.name
                categoryCode = product.categoryCode
                status = ProductStatusDb.valueOf(product.status.name)
            }
        } else {
            ProductMapper.toJpa(product)
        }

        val savedProduct = productRepo.save(persistedProduct)

        variantAttributeRepo.deleteAllByProductId(savedProduct.id)
        variantAttributeRepo.flush()

        val variantEntities = variantRepo.findAllByProductId(savedProduct.id)
        if (variantEntities.isNotEmpty()) {
            variantOptionRepo.deleteAllByVariantIdIn(variantEntities.map { it.id })
            variantOptionRepo.flush()
            variantRepo.deleteAllByProductId(savedProduct.id)
            variantRepo.flush()
        }

        val now = ProductMapper.now()

        val variantAttributeEntities = product.variantAttributeCodes().map { attributeCode ->
            ProductVariantAttributeJpaEntity(
                id = UUID.randomUUID(),
                product = savedProduct,
                attributeCode = attributeCode,
                createdAt = now
            )
        }
        if (variantAttributeEntities.isNotEmpty()) {
            variantAttributeRepo.saveAll(variantAttributeEntities)
        }

        val newVariantEntities = product.variants().map { variant ->
            VariantJpaEntity(
                id = variant.id.value,
                product = savedProduct,
                code = variant.code,
                signature = variant.signature,
                active = variant.active,
                barcode = variant.barcode,
                createdAt = variant.createdAt
            )
        }
        if (newVariantEntities.isNotEmpty()) {
            variantRepo.saveAll(newVariantEntities)
        }

        if (newVariantEntities.isNotEmpty()) {
            val variantById = newVariantEntities.associateBy { it.id }
            val optionEntities = product.variants().flatMap { variant ->
                variant.options().map { option ->
                    VariantOptionJpaEntity(
                        id = UUID.randomUUID(),
                        variant = variantById[variant.id.value] ?: error("Variant not persisted: ${variant.id.value}"),
                        attributeCode = option.attributeCode,
                        attributeValueCode = option.attributeValueCode
                    )
                }
            }
            if (optionEntities.isNotEmpty()) {
                variantOptionRepo.saveAll(optionEntities)
            }
        }

        return loadByCode(savedProduct.code)
            ?: error("Failed to reload saved product: ${savedProduct.code}")
    }

    @Transactional(readOnly = true)
    override fun findByCode(code: String): Product? =
        loadByCode(code.trim().lowercase())

    @Transactional(readOnly = true)
    override fun existsByCode(code: String): Boolean =
        productRepo.existsByCode(code.trim().lowercase())

    private fun loadByCode(code: String): Product? {
        val product = productRepo.findByCode(code) ?: return null
        val variantAttributes = variantAttributeRepo.findAllByProductId(product.id)
        val variants = variantRepo.findAllByProductId(product.id)
        val options = if (variants.isEmpty()) emptyList() else variantOptionRepo.findAllByVariantIdIn(variants.map { it.id })
        return ProductMapper.toDomain(product, variantAttributes, variants, options)
    }
}
