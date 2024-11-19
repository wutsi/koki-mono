package com.wutsi.koki.workflow.dto

import java.util.Date

data class CreateWorkflowInstanceRequest(
    val workflowId: Long = -1,
    val participants: List<Participant> = emptyList(),
    val approverUserId: Long? = null,
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    var parameters: Map<String, String> = emptyMap(),
)
