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
    val startedAt: Date? = null,
    val dueAt: Date? = null,
    val parameters: Map<String, String> = emptyMap(),
    val state: Map<String, Any> = emptyMap(),
    val activityInstances: List<ActivityInstanceSummary> = emptyList(),
    val creatorByUserId: Long? = null,
)
