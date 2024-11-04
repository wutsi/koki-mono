package com.wutsi.koki.workflow.server.domain

import com.wutsi.koki.workflow.dto.ApprovalStatus
import java.util.Date

data class ActivityInstanceEntity(
    val id: String = "",
    val activityId: Long = -1,
    val instanceId: String = "",
    var assigneeUserId: Long? = null,
    var approverUserId: Long? = null,
    var approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val createdAt: Date = Date(),
    var approvedAt: Date? = null,
    var startedAt: Date? = null,
    var doneAt: Date? = null
)
