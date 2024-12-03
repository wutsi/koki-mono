package com.wutsi.koki.workflow.dto

data class ActivitySummary(
    val id: Long = -1,
    val workflowId: Long = -1,
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = false,
    val requiresApproval: Boolean = false,
    val roleId: Long? = null,
    val formId: String? = null,
    val messageId: String? = null,
)
