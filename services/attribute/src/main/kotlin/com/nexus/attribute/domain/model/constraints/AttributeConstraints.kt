package com.nexus.attribute.domain.model.constraints

sealed interface AttributeConstraints

data class StringConstraints(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val regex: String? = null,
    val allowed: List<String>? = null
) : AttributeConstraints

data class NumberConstraints(
    val min: Double? = null,
    val max: Double? = null
) : AttributeConstraints

data class EnumConstraints(
    val allowed: List<String>
) : AttributeConstraints

data class DateConstraints(
    val min: String? = null, // ISO-8601 date e.g. "2026-01-31"
    val max: String? = null
) : AttributeConstraints

object NoConstraints : AttributeConstraints
