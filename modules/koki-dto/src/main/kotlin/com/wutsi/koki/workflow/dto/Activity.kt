package com.wutsi.koki.workflow.dto

import java.util.Date

data class Activity(
    val id: Long = -1,
    val index: Int = -1,
    val code: String = "",
    val type: ActivityType = ActivityType.UNKNOWN,
    val title: String = "",
    val description: String = "",
    val active: Boolean = true,
    val requiresApproval: Boolean = true,
    val createdAt: Date = Date(),
    val files: List<File> = emptyList(),
    val tags: List<Tag> = emptyList()
)
