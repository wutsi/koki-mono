package com.wutsi.koki.workflow.dto.event

data class ApprovalStartedEvent(
    val tenantId: Long = -1,
    val activityInstanceId: String = "",
    val workflowInstanceId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
