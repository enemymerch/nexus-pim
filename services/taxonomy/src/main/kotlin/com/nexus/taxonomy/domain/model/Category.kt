package com.nexus.taxonomy.domain.model

import com.nexus.taxonomy.domain.valueobject.CategoryId
import java.time.Instant
import java.util.UUID

class Category internal constructor(
    val id: CategoryId,
    val code: String,
    var name: String,
    var parentId: CategoryId?,
    var path: String,
    var depth: Int,
    val createdAt: Instant
) {
    fun rename(newName: String) {
        require(newName.isNotBlank()) { "Category name cannot be blank." }
        name = newName.trim()
    }

    fun moveTo(newParent: CategoryId?, newPath: String, newDepth: Int) {
        parentId = newParent
        path = newPath
        depth = newDepth
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun createRoot(code: String, name: String, now: Instant = Instant.now()): Category {
            val c = normalizeCode(code)
            validateCode(c)
            require(name.isNotBlank()) { "Category name cannot be blank." }

            return Category(
                id = CategoryId(UUID.randomUUID()),
                code = c,
                name = name.trim(),
                parentId = null,
                path = "/$c",
                depth = 0,
                createdAt = now
            )
        }

        fun createChild(
            code: String,
            name: String,
            parent: Category,
            now: Instant = Instant.now()
        ): Category {
            val c = normalizeCode(code)
            validateCode(c)
            require(name.isNotBlank()) { "Category name cannot be blank." }

            return Category(
                id = CategoryId(UUID.randomUUID()),
                code = c,
                name = name.trim(),
                parentId = parent.id,
                path = "${parent.path}/$c",
                depth = parent.depth + 1,
                createdAt = now
            )
        }

        fun normalizeCode(raw: String) = raw.trim().lowercase()

        fun validateCode(code: String) {
            require(code.isNotBlank()) { "Code cannot be blank." }
            require(CODE_REGEX.matches(code)) { "Invalid code '$code'." }
        }
    }
}
