package com.nexus.workflow.domain.port

import com.nexus.workflow.domain.model.WorkflowInstance

interface WorkflowStorePort {
    fun save(instance: WorkflowInstance): WorkflowInstance
    fun findByProductCode(productCode: String): WorkflowInstance?
}
