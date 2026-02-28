package com.nexus.workflow.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface WorkflowInstanceJpaRepository : JpaRepository<WorkflowInstanceJpaEntity, java.util.UUID> {
    fun findByProductCode(productCode: String): WorkflowInstanceJpaEntity?
}
