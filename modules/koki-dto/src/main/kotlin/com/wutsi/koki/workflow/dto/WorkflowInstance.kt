package com.wutsi.koki.workflow.dto

import com.wutsi.koki.workflow.server.domain.ActivityInstance
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
    val state: Map<String, String> = emptyMap(),
    val activityInstances: List<ActivityInstance> = emptyList(),
)
