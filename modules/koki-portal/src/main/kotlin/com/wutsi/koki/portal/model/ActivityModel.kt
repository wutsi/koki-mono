package com.wutsi.koki.portal.model

import com.wutsi.koki.workflow.dto.ActivityType

data class ActivityModel(
    val id: Long = -1,
    val workflowId: Long = -1,
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val type: ActivityType = ActivityType.UNKNOWN,
    val requiresApproval: Boolean = false,
    val role: RoleModel? = null,
)
