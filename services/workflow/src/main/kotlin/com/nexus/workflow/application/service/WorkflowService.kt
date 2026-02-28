package com.nexus.workflow.application.service

import com.nexus.workflow.domain.model.WorkflowAction
import com.nexus.workflow.domain.model.WorkflowInstance
import com.nexus.workflow.domain.port.WorkflowStorePort
import com.nexus.workflow.infrastructure.client.CatalogServiceClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class WorkflowService(
    private val store: WorkflowStorePort,
    private val catalogClient: CatalogServiceClient
) {

    @Transactional
    fun start(productCode: String, actor: String): WorkflowDetails {
        val normalizedCode = WorkflowInstance.normalizeCode(productCode)
        if (store.findByProductCode(normalizedCode) != null) {
            throw IllegalArgumentException("Workflow already exists for product: $normalizedCode")
        }
        catalogClient.requireProductExists(normalizedCode)
        val created = WorkflowInstance.start(normalizedCode, actor)
        return toDetails(store.save(created))
    }

    @Transactional
    fun transition(productCode: String, action: WorkflowAction, actor: String, comment: String?): WorkflowDetails {
        val instance = requireInstance(productCode)
        instance.transition(action = action, actor = actor, comment = comment)
        return toDetails(store.save(instance))
    }

    @Transactional(readOnly = true)
    fun get(productCode: String): WorkflowDetails =
        toDetails(requireInstance(productCode))

    @Transactional(readOnly = true)
    fun history(productCode: String): List<WorkflowTransitionDetails> =
        requireInstance(productCode).transitions.map {
            WorkflowTransitionDetails(
                fromState = it.fromState.name,
                action = it.action.name,
                toState = it.toState.name,
                actor = it.actor,
                comment = it.comment,
                happenedAt = it.happenedAt
            )
        }

    private fun requireInstance(productCode: String): WorkflowInstance {
        val normalizedCode = WorkflowInstance.normalizeCode(productCode)
        return store.findByProductCode(normalizedCode)
            ?: throw NoSuchElementException("Workflow not found for product: $normalizedCode")
    }

    private fun toDetails(instance: WorkflowInstance): WorkflowDetails =
        WorkflowDetails(
            productCode = instance.productCode,
            state = instance.state.name,
            createdAt = instance.createdAt,
            updatedAt = instance.updatedAt,
            history = instance.transitions.map {
                WorkflowTransitionDetails(
                    fromState = it.fromState.name,
                    action = it.action.name,
                    toState = it.toState.name,
                    actor = it.actor,
                    comment = it.comment,
                    happenedAt = it.happenedAt
                )
            }
        )
}

data class WorkflowDetails(
    val productCode: String,
    val state: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val history: List<WorkflowTransitionDetails>
)

data class WorkflowTransitionDetails(
    val fromState: String,
    val action: String,
    val toState: String,
    val actor: String,
    val comment: String?,
    val happenedAt: Instant
)
