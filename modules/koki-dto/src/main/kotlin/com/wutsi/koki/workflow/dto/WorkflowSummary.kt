package com.wutsi.koki.workflow.dto

import java.util.Date

data class WorkflowSummary(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val requiresApprover: Boolean = false,
)
