package com.nexus.attribute.application.constraints

import com.nexus.attribute.domain.enums.AttributeDataType
import com.nexus.attribute.domain.model.constraints.*
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Component
class ConstraintsSchemaValidator {

    fun validate(dataType: AttributeDataType, c: AttributeConstraints) {
        when (dataType) {
            AttributeDataType.STRING -> validateString(c)
            AttributeDataType.NUMBER -> validateNumber(c)
            AttributeDataType.ENUM -> validateEnum(c)
            AttributeDataType.DATE -> validateDate(c)
            AttributeDataType.BOOLEAN -> {
                if (c !is NoConstraints) error("BOOLEAN does not support constraints")
            }
            AttributeDataType.JSON -> Unit
        }
    }

    private fun validateString(c: AttributeConstraints) {
        if (c is NoConstraints) return
        require(c is StringConstraints) { "STRING constraints must be {minLength,maxLength,regex,allowed}" }

        c.minLength?.let { require(it >= 0) { "minLength must be >= 0" } }
        c.maxLength?.let { require(it >= 0) { "maxLength must be >= 0" } }

        if (c.minLength != null && c.maxLength != null) {
            require(c.minLength <= c.maxLength) { "minLength cannot be greater than maxLength" }
        }

        c.allowed?.let { require(it.isNotEmpty()) { "allowed cannot be empty" } }

        c.regex?.let {
            try { Regex(it) } catch (e: Exception) {
                throw IllegalArgumentException("Invalid regex: ${e.message}")
            }
        }
    }

    private fun validateNumber(c: AttributeConstraints) {
        if (c is NoConstraints) return
        require(c is NumberConstraints) { "NUMBER constraints must be {min,max}" }

        if (c.min != null && c.max != null) {
            require(c.min <= c.max) { "min cannot be greater than max" }
        }
    }

    private fun validateEnum(c: AttributeConstraints) {
        if (c is NoConstraints) throw IllegalArgumentException("ENUM constraints require 'allowed'")
        require(c is EnumConstraints) { "ENUM constraints must be {allowed:[...]}" }
        require(c.allowed.isNotEmpty()) { "allowed cannot be empty" }
    }

    private fun validateDate(c: AttributeConstraints) {
        if (c is NoConstraints) return
        require(c is DateConstraints) { "DATE constraints must be {min,max}" }

        val min = c.min?.parseIsoDateOrThrow("min")
        val max = c.max?.parseIsoDateOrThrow("max")
        if (min != null && max != null) require(!min.isAfter(max)) { "min date cannot be after max date" }
    }
}

private fun String.parseIsoDateOrThrow(field: String): LocalDate =
    try { LocalDate.parse(this) } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("Invalid ISO date for '$field': $this")
    }
