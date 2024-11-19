package com.wutsi.koki.workflow.dto

import java.util.Date

data class Workflow(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val requiresApprover: Boolean = false,
    val approverRoleId: Long? = null,
    val active: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val parameters: List<String> = emptyList(),
    val activities: List<Activity> = emptyList(),
    val flows: List<Flow> = emptyList(),
    val roleIds: List<Long> = emptyList(),
    val workflowInstanceCount: Long = 0L,
)
