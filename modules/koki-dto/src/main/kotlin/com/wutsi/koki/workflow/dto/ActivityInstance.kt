package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.Activity
import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowInstanceSummary
import com.wutsi.koki.workflow.dto.WorkflowStatus
import com.wutsi.koki.workflow.dto.WorkflowSummary
import java.util.Date

data class ActivityInstance(
    val id: String = "",
    val activity: Activity = Activity(),
    val workflow: WorkflowSummary = WorkflowSummary(),
    val workflowInstance: WorkflowInstanceSummary = WorkflowInstanceSummary(),
    val assigneeUserId: Long? = null,
    val approverUserId: Long? = null,
    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val approvedAt: Date? = null,
    val startedAt: Date? = null,
    val doneAt: Date? = null
)
