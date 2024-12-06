package com.wutsi.koki.workflow.server.engine.command

data class RunActivityCommand(
    val tenantId: Long = -1,
    val activityInstanceId: String = "",
    val workflowInstanceId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)
