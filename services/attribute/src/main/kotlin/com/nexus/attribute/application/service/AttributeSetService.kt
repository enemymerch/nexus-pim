package com.nexus.attribute.application.service

import com.nexus.attribute.application.constraints.ConstraintsCodec
import com.nexus.attribute.application.constraints.ConstraintsSchemaValidator
import com.nexus.attribute.domain.model.AttributeSet
import com.nexus.attribute.domain.port.AttributeSetStorePort
import com.nexus.attribute.domain.port.AttributeStorePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AttributeSetService(
    private val store: AttributeSetStorePort,
    private val attributeStore: AttributeStorePort,
    private val codec: ConstraintsCodec,
    private val schemaValidator: ConstraintsSchemaValidator
) {

    @Transactional
    fun createSet(code: String, name: String): AttributeSet {
        val set = AttributeSet.create(code, name)
        if (store.existsByCode(set.code)) {
            throw IllegalArgumentException("AttributeSet already exists: ${set.code}")
        }
        return store.save(set)
    }

    @Transactional
    fun addGroup(setCode: String, groupCode: String, name: String, sortOrder: Int): AttributeSet {
        val set = requireSet(setCode)
        set.addGroup(groupCode, name, sortOrder)
        return store.save(set)
    }

    @Transactional
    fun attachAttribute(
        setCode: String,
        attributeCode: String,
        required: Boolean,
        sortOrder: Int,
        groupCode: String?,
        constraintsJson: String?
    ): AttributeSet {
        val set = requireSet(setCode)

        val attr = attributeStore.findByCode(attributeCode)
            ?: throw NoSuchElementException("Attribute not found: ${attributeCode.trim().lowercase()}")

        // 1) parse typed constraints
        val typed = codec.parse(attr.dataType, constraintsJson)

        // 2) validate constraints schema for that datatype
        schemaValidator.validate(attr.dataType, typed)

        // 3) canonicalize JSON for persistence (stable form)
        val canonical = codec.toCanonicalJson(typed)

        // 4) attach to aggregate (store canonical string)
        set.attachAttribute(
            attributeCode = attr.code,
            required = required,
            sortOrder = sortOrder,
            groupCode = groupCode,
            constraintsJson = canonical
        )

        return store.save(set)
    }

    @Transactional(readOnly = true)
    fun get(setCode: String): AttributeSet =
        store.findByCode(setCode) ?: throw NoSuchElementException("AttributeSet not found: ${setCode.trim().lowercase()}")

    private fun requireSet(setCode: String): AttributeSet =
        store.findByCode(setCode) ?: throw NoSuchElementException("AttributeSet not found: ${setCode.trim().lowercase()}")
}
