package com.nexus.workflow.infrastructure.persistence

import com.nexus.workflow.domain.model.WorkflowAction
import com.nexus.workflow.domain.model.WorkflowState
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "workflow_transitions")
class WorkflowTransitionJpaEntity(
    @Id
    @Column(nullable = false)
    var id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_instance_id", nullable = false)
    var workflowInstance: WorkflowInstanceJpaEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "from_state", nullable = false, length = 16)
    var fromState: WorkflowState,

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 16)
    var action: WorkflowAction,

    @Enumerated(EnumType.STRING)
    @Column(name = "to_state", nullable = false, length = 16)
    var toState: WorkflowState,

    @Column(name = "actor", nullable = false, length = 128)
    var actor: String,

    @Column(name = "comment", columnDefinition = "text")
    var comment: String?,

    @Column(name = "happened_at", nullable = false)
    var happenedAt: Instant
)
