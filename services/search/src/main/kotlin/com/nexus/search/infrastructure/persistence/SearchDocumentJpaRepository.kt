package com.nexus.search.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface SearchDocumentJpaRepository : JpaRepository<SearchDocumentJpaEntity, UUID> {
    fun findByProductCode(productCode: String): SearchDocumentJpaEntity?

    @Query(
        value = """
            select * from search_documents d
            where (:q is null or d.searchable_text like concat('%', :q, '%'))
              and (:categoryCode is null or d.category_code = :categoryCode)
              and (:status is null or d.status = :status)
            order by d.updated_at desc
            limit :limit
        """,
        nativeQuery = true
    )
    fun search(
        @Param("q") q: String?,
        @Param("categoryCode") categoryCode: String?,
        @Param("status") status: String?,
        @Param("limit") limit: Int
    ): List<SearchDocumentJpaEntity>
}
