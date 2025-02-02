package com.wutsi.koki.workflow.dto

import java.util.Date

data class WorkflowInstanceSummary(
    val id: String = "",
    val workflowId: Long = -1,
    val approverUserId: Long? = null,
    val title: String = "",
    val status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val startAt: Date = Date(),
    val startedAt: Date? = null,
    val dueAt: Date? = null,
    val createdById: Long? = null,
    val doneAt: Date? = null,
)
