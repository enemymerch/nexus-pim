package com.nexus.workflow.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkflowTransitionJpaRepository : JpaRepository<WorkflowTransitionJpaEntity, UUID> {
    fun findAllByWorkflowInstanceIdOrderByHappenedAtAsc(workflowInstanceId: UUID): List<WorkflowTransitionJpaEntity>
    fun deleteAllByWorkflowInstanceId(workflowInstanceId: UUID)
}
