package com.nexus.content.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ContentEntryJpaRepository : JpaRepository<ContentEntryJpaEntity, java.util.UUID> {
    fun findAllByProductContentId(productContentId: java.util.UUID): List<ContentEntryJpaEntity>
    fun deleteAllByProductContentId(productContentId: java.util.UUID)
}
