package com.nexus.attribute.web.dto

import com.nexus.attribute.domain.model.AttributeSet

data class AttributeSetResponse(
    val code: String,
    val name: String,
    val groups: List<GroupResponse>,
    val items: List<ItemResponse>
) {
    data class GroupResponse(val code: String, val name: String, val sortOrder: Int)
    data class ItemResponse(
        val attributeCode: String,
        val required: Boolean,
        val sortOrder: Int,
        val groupCode: String?,
        val constraintsJson: String?
    )
}

fun AttributeSet.toResponse(): AttributeSetResponse =
    AttributeSetResponse(
        code = code,
        name = name,
        groups = groups().map { AttributeSetResponse.GroupResponse(it.code, it.name, it.sortOrder) },
        items = items().map { item ->
            val groupCode = item.groupId?.let { gid -> groups().firstOrNull { it.id == gid }?.code }
            AttributeSetResponse.ItemResponse(
                attributeCode = item.attributeCode,
                required = item.required,
                sortOrder = item.sortOrder,
                groupCode = groupCode,
                constraintsJson = item.constraintsJson
            )
        }
    )
