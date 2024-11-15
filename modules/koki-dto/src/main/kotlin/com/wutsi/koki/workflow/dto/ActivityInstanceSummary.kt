package com.wutsi.koki.workflow.dto

import java.util.Date

data class ActivityInstanceSummary(
    val id: String = "",
    val activityId: Long = -1,
    val workflowInstanceId: String = "",
    val assigneeUserId: Long? = null,
    val approverUserId: Long? = null,
    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val approvedAt: Date? = null,
    val startedAt: Date? = null,
    val doneAt: Date? = null
)
