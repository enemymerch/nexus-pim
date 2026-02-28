package com.nexus.attribute.domain.model

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.enums.AttributeScope
import java.time.Instant

class Attribute private constructor(
    val code: String,
    val dataType: AttributeDataType,
    val scope: AttributeScope,
    val localizable: Boolean,
    val channelOverridable: Boolean,
    val multiValue: Boolean,
    val createdAt: Instant
    ){
    companion object{
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun create(
            code: String,
            dataType: AttributeDataType,
            scope: AttributeScope,
            localizable: Boolean,
            channelOverridable: Boolean,
            multiValue: Boolean,
            now: Instant = Instant.now()
        ) : Attribute {
            val normalizedCode = normalizeCode(code)
            validateCode(normalizedCode)
            return Attribute(code = normalizedCode, dataType = dataType, scope = scope, localizable = localizable, channelOverridable = channelOverridable, multiValue = multiValue, createdAt =  now)
        }

        private fun validateCode(code: String) {
            require(code.isNotBlank()) { "Attribute code cannot be blank." }
            require(CODE_REGEX.matches(code)) {
                "Invalid attribute code '$code'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
        }

        fun normalizeCode(code: String): String = code.trim().lowercase()


    }
    override fun toString(): String =
        "Attribute(code='$code', dataType=$dataType, scope=$scope, localizable=$localizable, channelOverridable=$channelOverridable, multiValue=$multiValue)"
}