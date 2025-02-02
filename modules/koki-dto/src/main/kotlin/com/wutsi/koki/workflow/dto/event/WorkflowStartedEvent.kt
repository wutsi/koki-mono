package com.wutsi.koki.workflow.dto.event

data class WorkflowStartedEvent(
    val tenantId: Long = -1,
    val workflowInstanceId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
