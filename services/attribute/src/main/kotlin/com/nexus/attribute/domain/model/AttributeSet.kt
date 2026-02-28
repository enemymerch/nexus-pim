package com.nexus.attribute.domain.model

import com.nexus.attribute.domain.valueobject.AttributeGroupId
import com.nexus.attribute.domain.valueobject.AttributeSetId
import com.nexus.attribute.domain.valueobject.AttributeSetItemId
import java.time.Instant
import java.util.UUID

class AttributeSet internal constructor(
    val id: AttributeSetId,
    val code: String,
    var name: String,
    private val groups: MutableList<AttributeGroup>,
    private val items: MutableList<AttributeSetItem>,
    val createdAt: Instant
) {

    fun groups(): List<AttributeGroup> = groups.sortedBy { it.sortOrder }
    fun items(): List<AttributeSetItem> =
        items.sortedWith(compareBy<AttributeSetItem>({ it.groupId?.value?.toString() ?: "ZZZ" }, { it.sortOrder }))

    fun rename(newName: String) {
        require(newName.isNotBlank()) { "AttributeSet name cannot be blank." }
        name = newName.trim()
    }

    fun addGroup(code: String, name: String, sortOrder: Int, now: Instant = Instant.now()): AttributeGroup {
        val c = normalizeCode(code)
        require(groups.none { it.code == c }) { "Group already exists: $c" }
        val group = AttributeGroup(
            id = AttributeGroupId(UUID.randomUUID()),
            code = c,
            name = name.trim().also { require(it.isNotBlank()) },
            sortOrder = sortOrder,
            createdAt = now
        )
        groups.add(group)
        return group
    }

    fun attachAttribute(
        attributeCode: String,
        required: Boolean,
        sortOrder: Int,
        groupCode: String?,
        constraintsJson: String?,
        now: Instant = Instant.now()
    ): AttributeSetItem {
        val attrCode = normalizeCode(attributeCode)
        require(items.none { it.attributeCode == attrCode }) { "Attribute already attached: $attrCode" }

        val groupId = groupCode?.let { gc ->
            val normalizedGroup = normalizeCode(gc)
            val group = groups.find { it.code == normalizedGroup }
                ?: throw IllegalArgumentException("Group not found in set '$code': $normalizedGroup")
            group.id
        }

        val item = AttributeSetItem(
            id = AttributeSetItemId(UUID.randomUUID()),
            attributeCode = attrCode,
            required = required,
            sortOrder = sortOrder,
            groupId = groupId,
            constraintsJson = constraintsJson,
            createdAt = now
        )
        items.add(item)
        return item
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun create(code: String, name: String, now: Instant = Instant.now()): AttributeSet {
            val c = normalizeCode(code)
            validateCode(c)
            require(name.isNotBlank()) { "AttributeSet name cannot be blank." }

            return AttributeSet(
                id = AttributeSetId(UUID.randomUUID()),
                code = c,
                name = name.trim(),
                groups = mutableListOf(),
                items = mutableListOf(),
                createdAt = now
            )
        }

        fun normalizeCode(raw: String): String = raw.trim().lowercase()

        fun validateCode(code: String) {
            require(code.isNotBlank()) { "Code cannot be blank." }
            require(CODE_REGEX.matches(code)) {
                "Invalid code '$code'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
        }
    }
}
