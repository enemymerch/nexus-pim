package com.nexus.workflow.web.dto

import com.nexus.workflow.application.service.WorkflowDetails
import com.nexus.workflow.application.service.WorkflowTransitionDetails
import com.nexus.workflow.domain.model.WorkflowAction
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class StartWorkflowRequest(
    @field:NotBlank val actor: String
)

data class TransitionWorkflowRequest(
    @field:NotNull val action: WorkflowAction,
    @field:NotBlank val actor: String,
    val comment: String? = null
)

data class WorkflowResponse(
    val productCode: String,
    val state: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val history: List<WorkflowTransitionResponse>
)

data class WorkflowTransitionResponse(
    val fromState: String,
    val action: String,
    val toState: String,
    val actor: String,
    val comment: String?,
    val happenedAt: Instant
)

fun WorkflowDetails.toResponse(): WorkflowResponse =
    WorkflowResponse(
        productCode = productCode,
        state = state,
        createdAt = createdAt,
        updatedAt = updatedAt,
        history = history.map(WorkflowTransitionDetails::toResponse)
    )

fun WorkflowTransitionDetails.toResponse(): WorkflowTransitionResponse =
    WorkflowTransitionResponse(
        fromState = fromState,
        action = action,
        toState = toState,
        actor = actor,
        comment = comment,
        happenedAt = happenedAt
    )
