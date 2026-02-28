package com.nexus.attribute.infrastructure.adapter

import com.nexus.attribute.domain.model.AttributeSet
import com.nexus.attribute.domain.port.AttributeSetStorePort
import com.nexus.attribute.infrastructure.persistence.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Repository
class AttributeSetStorePostgresAdapter(
    private val setRepo: AttributeSetJpaRepository,
    private val groupRepo: AttributeGroupJpaRepository,
    private val itemRepo: AttributeSetItemJpaRepository,
    private val attributeRepo: AttributeJpaRepository
) : AttributeSetStorePort {

    @Transactional
    override fun save(set: AttributeSet): AttributeSet {
        // Upsert set
        val existing = setRepo.findByCode(set.code)

        val persistedSet = if (existing != null) {
            existing.apply { name = set.name }
        } else {
            AttributeSetMapper.toJpaSet(set)
        }
        val savedSet = setRepo.save(persistedSet)

        // For simplicity: replace children (groups/items) on every save.
        // Later we can do diff-based updates.
        // Delete existing children
        val existingGroups = groupRepo.findAllByAttributeSetId(savedSet.id)
        val existingItems = itemRepo.findAllByAttributeSetId(savedSet.id)
        if (existingItems.isNotEmpty()) itemRepo.deleteAll(existingItems)
        if (existingGroups.isNotEmpty()) groupRepo.deleteAll(existingGroups)

        // Recreate groups
        val groupEntitiesByCode = set.groups().associate { g ->
            val ge = groupRepo.save(
                AttributeGroupJpaEntity(
                    id = g.id.value,
                    attributeSet = savedSet,
                    code = g.code,
                    name = g.name,
                    sortOrder = g.sortOrder,
                    createdAt = g.createdAt
                )
            )
            g.code to ge
        }

        // Recreate items (need AttributeJpaEntity)
        val now = Instant.now()
        val itemEntities = set.items().map { it ->
            val attr = attributeRepo.findByCode(it.attributeCode)
                ?: error("Attribute not found: ${it.attributeCode} (required for set item persistence)")

            val groupEntity = it.groupId?.let { gid ->
                // map by group code? we only have groupId; easiest: find in current set groups by id
                val groupCode = set.groups().firstOrNull { g -> g.id == gid }?.code
                    ?: error("Group not found in aggregate for item: ${gid.value}")
                groupEntitiesByCode[groupCode]
            }

            AttributeSetItemJpaEntity(
                id = it.id.value,
                attributeSet = savedSet,
                attribute = attr,
                group = groupEntity,
                required = it.required,
                sortOrder = it.sortOrder,
                constraintsJson = it.constraintsJson,
                createdAt = it.createdAt
            )
        }

        if (itemEntities.isNotEmpty()) itemRepo.saveAll(itemEntities)

        // Reload and return domain
        return load(savedSet.code) ?: error("Failed to reload saved set: ${savedSet.code}")
    }

    @Transactional(readOnly = true)
    override fun findByCode(code: String): AttributeSet? {
        val c = code.trim().lowercase()
        return load(c)
    }

    override fun existsByCode(code: String): Boolean =
        setRepo.existsByCode(code.trim().lowercase())

    private fun load(code: String): AttributeSet? {
        val set = setRepo.findByCode(code) ?: return null
        val groups = groupRepo.findAllByAttributeSetId(set.id)
        val items = itemRepo.findAllByAttributeSetId(set.id)
        return AttributeSetMapper.toDomain(set, groups, items)
    }
}
