package com.nexus.workflow.infrastructure.persistence

import com.nexus.workflow.domain.model.WorkflowInstance
import com.nexus.workflow.domain.model.WorkflowTransition
import com.nexus.workflow.domain.valueobject.WorkflowInstanceId
import com.nexus.workflow.domain.valueobject.WorkflowTransitionId

object WorkflowMapper {
    fun toJpaRoot(instance: WorkflowInstance): WorkflowInstanceJpaEntity =
        WorkflowInstanceJpaEntity(
            id = instance.id.value,
            productCode = instance.productCode,
            state = instance.state,
            createdAt = instance.createdAt,
            updatedAt = instance.updatedAt
        )

    fun toJpaTransition(transition: WorkflowTransition, root: WorkflowInstanceJpaEntity): WorkflowTransitionJpaEntity =
        WorkflowTransitionJpaEntity(
            id = transition.id.value,
            workflowInstance = root,
            fromState = transition.fromState,
            action = transition.action,
            toState = transition.toState,
            actor = transition.actor,
            comment = transition.comment,
            happenedAt = transition.happenedAt
        )

    fun toDomain(root: WorkflowInstanceJpaEntity, transitions: List<WorkflowTransitionJpaEntity>): WorkflowInstance =
        WorkflowInstance(
            id = WorkflowInstanceId(root.id),
            productCode = root.productCode,
            state = root.state,
            transitions = transitions.map {
                WorkflowTransition(
                    id = WorkflowTransitionId(it.id),
                    fromState = it.fromState,
                    action = it.action,
                    toState = it.toState,
                    actor = it.actor,
                    comment = it.comment,
                    happenedAt = it.happenedAt
                )
            }.toMutableList(),
            createdAt = root.createdAt,
            updatedAt = root.updatedAt
        )
}
