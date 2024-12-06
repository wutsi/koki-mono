package com.wutsi.koki.workflow.dto

import java.util.Date

data class LogEntrySummary(
    val id: String = "",
    val workflowInstanceId: String = "",
    val activityInstanceId: String?,
    val type: LogEntryType = LogEntryType.UNKNOWN,
    val message: String = "",
    val stackTrace: String = "",
    val createdAt: Date = Date(),
)
