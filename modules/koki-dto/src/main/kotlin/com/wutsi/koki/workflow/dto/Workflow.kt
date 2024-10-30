package com.wutsi.koki.workflow.dto

import java.util.Date

data class Workflow(
    val id: Long = -1,
    val title: String = "",
    val description: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val activities: List<Activity> = emptyList(),
)
