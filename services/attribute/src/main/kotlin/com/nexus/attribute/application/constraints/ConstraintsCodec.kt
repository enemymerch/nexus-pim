package com.nexus.attribute.application.constraints

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.model.constraints.*
import org.springframework.stereotype.Component

@Component
class ConstraintsCodec(
    private val om: ObjectMapper
) {
    fun parse(dataType: AttributeDataType, raw: String?): AttributeConstraints {
        if (raw.isNullOrBlank()) return NoConstraints

        val node = try {
            om.readTree(raw)
        } catch (e: Exception) {
            throw IllegalArgumentException("constraintsJson is not valid JSON: ${e.message}")
        }

        if (!node.isObject) throw IllegalArgumentException("constraintsJson must be a JSON object")

        return when (dataType) {
            AttributeDataType.STRING -> parseString(node)
            AttributeDataType.NUMBER -> parseNumber(node)
            AttributeDataType.ENUM -> parseEnum(node)
            AttributeDataType.DATE -> parseDate(node)
            AttributeDataType.BOOLEAN -> {
                if (node.fieldNames().hasNext()) throw IllegalArgumentException("BOOLEAN attributes do not support constraints")
                NoConstraints
            }
            AttributeDataType.JSON -> NoConstraints // ileride JSON schema ekleyebiliriz
        }
    }

    /**
     * Canonical JSON string to persist (stable ordering via Jackson).
     */
    fun toCanonicalJson(constraints: AttributeConstraints): String? {
        if (constraints is NoConstraints) return null
        val node: ObjectNode = om.createObjectNode()

        when (constraints) {
            is StringConstraints -> {
                constraints.minLength?.let { node.put("minLength", it) }
                constraints.maxLength?.let { node.put("maxLength", it) }
                constraints.regex?.let { node.put("regex", it) }
                constraints.allowed?.let { node.putArray("allowed").addAll(it.map(om::valueToTree)) }
            }
            is NumberConstraints -> {
                constraints.min?.let { node.put("min", it) }
                constraints.max?.let { node.put("max", it) }
            }
            is EnumConstraints -> {
                node.putArray("allowed").addAll(constraints.allowed.map(om::valueToTree))
            }
            is DateConstraints -> {
                constraints.min?.let { node.put("min", it) }
                constraints.max?.let { node.put("max", it) }
            }
            NoConstraints -> return null
        }

        return om.writeValueAsString(node)
    }

    private fun parseString(node: JsonNode): StringConstraints =
        StringConstraints(
            minLength = node.getIntOrNull("minLength"),
            maxLength = node.getIntOrNull("maxLength"),
            regex = node.getTextOrNull("regex"),
            allowed = node.getStringListOrNull("allowed")
        )

    private fun parseNumber(node: JsonNode): NumberConstraints =
        NumberConstraints(
            min = node.getDoubleOrNull("min"),
            max = node.getDoubleOrNull("max")
        )

    private fun parseEnum(node: JsonNode): EnumConstraints {
        val allowed = node.getStringListOrNull("allowed")
            ?: throw IllegalArgumentException("ENUM constraints must include 'allowed' array")
        return EnumConstraints(allowed = allowed)
    }

    private fun parseDate(node: JsonNode): DateConstraints =
        DateConstraints(
            min = node.getTextOrNull("min"),
            max = node.getTextOrNull("max")
        )
}

private fun JsonNode.getTextOrNull(field: String): String? =
    this.get(field)?.takeIf { !it.isNull }?.asText()

private fun JsonNode.getIntOrNull(field: String): Int? =
    this.get(field)?.takeIf { it.isInt || it.isLong }?.asInt()

private fun JsonNode.getDoubleOrNull(field: String): Double? =
    this.get(field)?.takeIf { it.isNumber }?.asDouble()

private fun JsonNode.getStringListOrNull(field: String): List<String>? {
    val arr = this.get(field) ?: return null
    if (!arr.isArray) throw IllegalArgumentException("'$field' must be an array")
    return arr.map {
        if (!it.isTextual) throw IllegalArgumentException("'$field' must contain only strings")
        it.asText()
    }
}
