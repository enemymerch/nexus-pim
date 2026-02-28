package com.nexus.workflow.infrastructure.persistence

import com.nexus.workflow.domain.model.WorkflowState
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "workflow_instances")
class WorkflowInstanceJpaEntity(
    @Id
    @Column(nullable = false)
    var id: UUID,

    @Column(name = "product_code", nullable = false, unique = true, length = 64)
    var productCode: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    var state: WorkflowState,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant
)
