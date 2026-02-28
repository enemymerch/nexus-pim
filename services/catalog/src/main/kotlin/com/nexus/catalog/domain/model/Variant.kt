package com.nexus.catalog.domain.model

import com.nexus.catalog.domain.valueobject.VariantId
import java.time.Instant

class Variant internal constructor(
    val id: VariantId,
    val code: String,
    val signature: String,
    var active: Boolean,
    var barcode: String?,
    private val options: MutableSet<VariantOption>,
    val createdAt: Instant
) {
    fun options(): Set<VariantOption> = options.toSet()

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun normalizeCode(raw: String): String = raw.trim().lowercase()

        fun validateCode(code: String) {
            require(code.isNotBlank()) { "Variant code cannot be blank." }
            require(CODE_REGEX.matches(code)) {
                "Invalid variant code '$code'. Use 2-64 chars: lowercase letters, numbers, '_' or '-'."
            }
        }
    }
}
