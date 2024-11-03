package com.wutsi.koki.workflow.dto

import java.util.Date

data class TaskEvent(
    val id: Long = -1,
    val taskId: Long = -1,
    val type: TaskEventType = TaskEventType.UNKNOWN,
    val status: WorkflowStatus? = null,
    val approval: ApprovalStatus? = null,
    val comment: String = "",
    val createdAt: Date = Date(),
    val createdByUserId: Long = -1,
)
