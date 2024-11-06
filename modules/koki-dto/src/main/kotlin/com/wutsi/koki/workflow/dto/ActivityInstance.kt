package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.ApprovalStatus
import java.util.Date

data class ActivityInstance(
    val id: String = "",
    val activityId: Long = -1,
    val instanceId: String = "",
    val assigneeUserId: Long? = null,
    val approverUserId: Long? = null,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val approvedAt: Date? = null,
    val startedAt: Date? = null,
    val doneAt: Date? = null
)
