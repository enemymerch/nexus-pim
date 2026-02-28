package com.nexus.taxonomy.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "categories")
class CategoryJpaEntity(

    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(nullable = false, unique = true, length = 64)
    var code: String,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(name = "parent_id")
    var parentId: UUID?,

    @Column(nullable = false)
    var depth: Int,

    @Column(nullable = false, length = 1024)
    var path: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant
)
