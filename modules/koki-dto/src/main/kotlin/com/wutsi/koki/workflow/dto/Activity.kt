package com.wutsi.koki.workflow.dto

import java.util.Date

data class Activity(
    val id: Long = -1,
    val workflowId: Long = -1,
    val code: String = "",
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val description: String? = null,
    val active: Boolean = true,
    val requiresApproval: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val tags: Map<String, String> = emptyMap(),
    val predecessors: List<String> = emptyList(),
)
