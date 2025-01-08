package com.wutsi.koki.note.dto

import java.util.Date

data class NoteSummary(
    val id: Long = -1,
    val subject: String = "",
    val summary: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
