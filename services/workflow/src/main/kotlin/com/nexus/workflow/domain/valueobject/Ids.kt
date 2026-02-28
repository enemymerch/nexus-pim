package com.nexus.workflow.domain.valueobject

import java.util.UUID

@JvmInline value class WorkflowInstanceId(val value: UUID)
@JvmInline value class WorkflowTransitionId(val value: UUID)
