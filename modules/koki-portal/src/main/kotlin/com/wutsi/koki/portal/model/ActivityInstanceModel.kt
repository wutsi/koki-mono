package com.wutsi.koki.portal.model

import com.wutsi.koki.workflow.dto.ApprovalStatus
import com.wutsi.koki.workflow.dto.WorkflowStatus
import java.util.Date

data class ActivityInstanceModel(
    val id: String = "",
    val workflowInstance: WorkflowInstanceModel = WorkflowInstanceModel(),
    val activity: ActivityModel = ActivityModel(),
    val assignee: UserModel? = null,
    val approver: UserModel? = null,
    var status: WorkflowStatus = WorkflowStatus.UNKNOWN,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val approvedAt: Date? = null,
    val startedAt: Date? = null,
    val doneAt: Date? = null,
    val createdAtText: String = "",
    val approvedAtText: String? = null,
    val startedAtText: String? = null,
    val doneAtText: String? = null,
) {
    val running: Boolean
        get() = status == WorkflowStatus.RUNNING

    val url: String
        get() = "/workflows/activities/$id"

    val completeUrl: String
        get() = "/workflows/activities/$id/complete"
}
