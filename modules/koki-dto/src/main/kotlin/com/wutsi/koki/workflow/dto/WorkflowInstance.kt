package com.wutsi.koki.workflow.dto

import java.util.Date

data class WorkflowInstance(
    val id: String = "",
    val workflowId: Long = -1,
    val participants: List<Participant> = emptyList(),
    val approverUserId: Long? = null,
    val title: String = "",
    val status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val startAt: Date = Date(),
    val startedAt: Date? = null,
    val dueAt: Date? = null,
    val state: Map<String, Any> = emptyMap(),
    val activityInstances: List<ActivityInstanceSummary> = emptyList(),
    val createdById: Long? = null,
    val doneAt: Date? = null,
)
