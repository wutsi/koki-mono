package com.wutsi.koki.workflow.dto

import java.util.Date

data class WorkflowInstance(
    val id: String = "",
    val workflowId: Long = -1,
    val participants: List<Participant> = emptyList(),
    val approverUserId: Long? = null,
    val status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val startAt: Date = Date(),
    val dueAt: Date? = null,
    var parameters: Map<String, String>,
)
