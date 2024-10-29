package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Activity(
    val id: Long = -1,
    val index: Int = -1,
    val type: ActivityType = ActivityType.UNKNOWN,
    val title: String = "",
    val description: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val documents: List<Document> = emptyList()
)
