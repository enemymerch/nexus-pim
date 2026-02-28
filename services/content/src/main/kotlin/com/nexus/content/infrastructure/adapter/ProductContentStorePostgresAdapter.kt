package com.nexus.content.infrastructure.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.nexus.content.domain.model.ProductContent
import com.nexus.content.domain.port.ProductContentStorePort
import com.nexus.content.infrastructure.persistence.ContentEntryJpaEntity
import com.nexus.content.infrastructure.persistence.ContentEntryJpaRepository
import com.nexus.content.infrastructure.persistence.ProductContentJpaRepository
import com.nexus.content.infrastructure.persistence.ProductContentMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProductContentStorePostgresAdapter(
    private val contentRepo: ProductContentJpaRepository,
    private val entryRepo: ContentEntryJpaRepository,
    private val objectMapper: ObjectMapper
) : ProductContentStorePort {

    @Transactional
    override fun save(content: ProductContent): ProductContent {
        val existing = contentRepo.findByProductCode(content.productCode)
        val root = if (existing != null) {
            existing.apply {
                defaultLocale = content.defaultLocale
                updatedAt = content.updatedAt
            }
        } else {
            ProductContentMapper.toJpa(content)
        }

        val savedRoot = contentRepo.save(root)

        entryRepo.deleteAllByProductContentId(savedRoot.id)
        entryRepo.flush()

        val entries = content.entries().map { entry ->
            ContentEntryJpaEntity(
                id = entry.id.value,
                productContent = savedRoot,
                locale = entry.locale,
                channel = entry.channel,
                title = entry.title,
                shortDescription = entry.shortDescription,
                description = entry.description,
                bulletsJson = objectMapper.writeValueAsString(entry.bullets),
                createdAt = entry.createdAt,
                updatedAt = entry.updatedAt
            )
        }

        if (entries.isNotEmpty()) {
            entryRepo.saveAll(entries)
        }

        return findByProductCode(content.productCode)
            ?: error("Failed to reload saved content: ${content.productCode}")
    }

    @Transactional(readOnly = true)
    override fun findByProductCode(productCode: String): ProductContent? {
        val root = contentRepo.findByProductCode(productCode.trim().lowercase()) ?: return null
        val entries = entryRepo.findAllByProductContentId(root.id)
        return ProductContentMapper.toDomain(root, entries, objectMapper)
    }

    @Transactional(readOnly = true)
    override fun existsByProductCode(productCode: String): Boolean =
        contentRepo.existsByProductCode(productCode.trim().lowercase())
}
