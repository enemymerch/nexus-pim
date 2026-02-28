package com.nexus.workflow.domain.model

import com.nexus.workflow.domain.valueobject.WorkflowInstanceId
import com.nexus.workflow.domain.valueobject.WorkflowTransitionId
import java.time.Instant
import java.util.UUID

enum class WorkflowState {
    DRAFT,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    PUBLISHED,
    ARCHIVED
}

enum class WorkflowAction {
    SUBMIT,
    APPROVE,
    REJECT,
    PUBLISH,
    ARCHIVE,
    REOPEN
}

data class WorkflowTransition(
    val id: WorkflowTransitionId = WorkflowTransitionId(UUID.randomUUID()),
    val fromState: WorkflowState,
    val action: WorkflowAction,
    val toState: WorkflowState,
    val actor: String,
    val comment: String?,
    val happenedAt: Instant
)

class WorkflowInstance internal constructor(
    val id: WorkflowInstanceId,
    val productCode: String,
    var state: WorkflowState,
    val transitions: MutableList<WorkflowTransition>,
    val createdAt: Instant,
    var updatedAt: Instant
) {

    fun transition(action: WorkflowAction, actor: String, comment: String?, now: Instant = Instant.now()): WorkflowTransition {
        val normalizedActor = actor.trim()
        require(normalizedActor.isNotBlank()) { "actor cannot be blank." }

        val target = resolveTransition(state, action)
            ?: throw IllegalStateException("Action '$action' is not allowed from state '$state'.")

        val transition = WorkflowTransition(
            fromState = state,
            action = action,
            toState = target,
            actor = normalizedActor,
            comment = comment?.trim()?.takeIf { it.isNotBlank() },
            happenedAt = now
        )
        state = target
        transitions += transition
        updatedAt = now
        return transition
    }

    companion object {
        private val CODE_REGEX = Regex("^[a-z0-9_\\-]{2,64}$")

        fun start(productCode: String, actor: String, now: Instant = Instant.now()): WorkflowInstance {
            val normalizedCode = normalizeCode(productCode)
            val normalizedActor = actor.trim()
            require(normalizedActor.isNotBlank()) { "actor cannot be blank." }

            val instance = WorkflowInstance(
                id = WorkflowInstanceId(UUID.randomUUID()),
                productCode = normalizedCode,
                state = WorkflowState.DRAFT,
                transitions = mutableListOf(),
                createdAt = now,
                updatedAt = now
            )
            instance.transitions += WorkflowTransition(
                fromState = WorkflowState.DRAFT,
                action = WorkflowAction.REOPEN,
                toState = WorkflowState.DRAFT,
                actor = normalizedActor,
                comment = "Workflow started",
                happenedAt = now
            )
            return instance
        }

        fun normalizeCode(raw: String): String {
            val normalized = raw.trim().lowercase()
            require(CODE_REGEX.matches(normalized)) { "Invalid product code '$normalized'." }
            return normalized
        }

        private fun resolveTransition(current: WorkflowState, action: WorkflowAction): WorkflowState? =
            when (current) {
                WorkflowState.DRAFT -> when (action) {
                    WorkflowAction.SUBMIT -> WorkflowState.IN_REVIEW
                    WorkflowAction.ARCHIVE -> WorkflowState.ARCHIVED
                    else -> null
                }
                WorkflowState.IN_REVIEW -> when (action) {
                    WorkflowAction.APPROVE -> WorkflowState.APPROVED
                    WorkflowAction.REJECT -> WorkflowState.REJECTED
                    WorkflowAction.ARCHIVE -> WorkflowState.ARCHIVED
                    else -> null
                }
                WorkflowState.APPROVED -> when (action) {
                    WorkflowAction.PUBLISH -> WorkflowState.PUBLISHED
                    WorkflowAction.REOPEN -> WorkflowState.DRAFT
                    WorkflowAction.ARCHIVE -> WorkflowState.ARCHIVED
                    else -> null
                }
                WorkflowState.REJECTED -> when (action) {
                    WorkflowAction.REOPEN -> WorkflowState.DRAFT
                    WorkflowAction.ARCHIVE -> WorkflowState.ARCHIVED
                    else -> null
                }
                WorkflowState.PUBLISHED -> when (action) {
                    WorkflowAction.ARCHIVE -> WorkflowState.ARCHIVED
                    WorkflowAction.REOPEN -> WorkflowState.DRAFT
                    else -> null
                }
                WorkflowState.ARCHIVED -> when (action) {
                    WorkflowAction.REOPEN -> WorkflowState.DRAFT
                    else -> null
                }
            }
    }
}
