package com.wutsi.koki.workflow.dto.event

data class WorkflowDoneEvent(
    val tenantId: Long = -1,
    val workflowInstanceId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
