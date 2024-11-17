package com.wutsi.koki.portal.model

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import java.util.Date

data class ActivityInstanceModel(
    val id: String = "",
    val activity: ActivityModel = ActivityModel(),
    val assigneeId: Long? = null,
    val approverId: Long? = null,
    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val approvedAt: Date? = null,
    val startedAt: Date? = null,
    val doneAt: Date? = null
)
