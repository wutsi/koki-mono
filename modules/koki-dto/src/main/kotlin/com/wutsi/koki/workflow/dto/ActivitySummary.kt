package com.wutsi.koki.workflow.dto

import java.util.Date

data class ActivitySummary(
    val id: Long = -1,
    val workflowId: Long = -1,
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = false,
    val requiresApproval: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val roleId: Long? = null,
    val formId: String? = null,
)
