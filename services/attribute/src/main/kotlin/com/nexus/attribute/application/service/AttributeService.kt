package com.nexus.attribute.application.service

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.enums.AttributeScope
import com.nexus.attribute.domain.model.Attribute
import com.nexus.attribute.domain.port.AttributeStorePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AttributeService(
    private val store: AttributeStorePort
) {

    @Transactional
    fun create(
        code: String,
        dataType: AttributeDataType,
        scope: AttributeScope,
        localizable: Boolean,
        channelOverridable: Boolean,
        multiValue: Boolean
    ): Attribute {
        val normalized = Attribute.normalizeCode(code)

        if (store.existsByCode(normalized)) {
            throw IllegalArgumentException("Attribute already exists: $normalized")
        }

        val attr = Attribute.create(
            code = normalized,
            dataType = dataType,
            scope = scope,
            localizable = localizable,
            channelOverridable = channelOverridable,
            multiValue = multiValue
        )

        return store.save(attr)
    }

    @Transactional(readOnly = true)
    fun getByCode(code: String): Attribute =
        store.findByCode(code)
            ?: throw NoSuchElementException("Attribute not found: ${code.trim().lowercase()}")

    @Transactional(readOnly = true)
    fun list(scope: AttributeScope?): List<Attribute> =
        store.findAll().filter { scope == null || it.scope == scope }
}
