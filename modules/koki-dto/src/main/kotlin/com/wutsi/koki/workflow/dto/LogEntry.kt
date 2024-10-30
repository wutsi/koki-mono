package com.wutsi.koki.workflow.dto

import java.util.Date

data class LogEntry(
    val id: Long = -1,
    val taskId: Long = -1,
    val ticketId: Long = -1,
    val category: LogCategory = LogCategory.UNKNOWN,
    val description: String = "",
    val stackTrace: String? = null,
    val createdAt: Date = Date(),
)
