package com.wutsi.koki.form.event

data class WorkflowStartedEvent(
    val tenantId: Long = -1,
    val workflowInstanceId: String = "",
)
