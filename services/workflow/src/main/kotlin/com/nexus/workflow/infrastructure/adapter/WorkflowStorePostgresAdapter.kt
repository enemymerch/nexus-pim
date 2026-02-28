package com.nexus.workflow.infrastructure.adapter

import com.nexus.workflow.domain.model.WorkflowInstance
import com.nexus.workflow.domain.port.WorkflowStorePort
import com.nexus.workflow.infrastructure.persistence.WorkflowInstanceJpaEntity
import com.nexus.workflow.infrastructure.persistence.WorkflowInstanceJpaRepository
import com.nexus.workflow.infrastructure.persistence.WorkflowMapper
import com.nexus.workflow.infrastructure.persistence.WorkflowTransitionJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class WorkflowStorePostgresAdapter(
    private val instanceRepo: WorkflowInstanceJpaRepository,
    private val transitionRepo: WorkflowTransitionJpaRepository
) : WorkflowStorePort {

    @Transactional
    override fun save(instance: WorkflowInstance): WorkflowInstance {
        val existing = instanceRepo.findByProductCode(instance.productCode)
        val root = if (existing != null) {
            existing.apply {
                state = instance.state
                updatedAt = instance.updatedAt
            }
        } else {
            WorkflowMapper.toJpaRoot(instance)
        }
        val savedRoot = instanceRepo.save(root)

        transitionRepo.deleteAllByWorkflowInstanceId(savedRoot.id)
        transitionRepo.flush()

        val transitions = instance.transitions.map { WorkflowMapper.toJpaTransition(it, savedRoot) }
        if (transitions.isNotEmpty()) {
            transitionRepo.saveAll(transitions)
        }
        return reload(savedRoot)
    }

    @Transactional(readOnly = true)
    override fun findByProductCode(productCode: String): WorkflowInstance? {
        val normalizedCode = productCode.trim().lowercase()
        val root = instanceRepo.findByProductCode(normalizedCode) ?: return null
        return reload(root)
    }

    private fun reload(root: WorkflowInstanceJpaEntity): WorkflowInstance {
        val transitions = transitionRepo.findAllByWorkflowInstanceIdOrderByHappenedAtAsc(root.id)
        return WorkflowMapper.toDomain(root, transitions)
    }
}
