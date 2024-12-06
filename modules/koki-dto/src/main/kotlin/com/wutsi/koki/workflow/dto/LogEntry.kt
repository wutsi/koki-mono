package com.wutsi.koki.workflow.dto

import java.util.Date

data class LogEntry(
    val id: String = "",
    val workflowInstanceId: String = "",
    val activityInstanceId: String? = null,
    val type: LogEntryType = LogEntryType.UNKNOWN,
    val message: String = "",
    val metadata: Map<String, Any> = emptyMap(),
    val stackTrace: String? = null,
    val createdAt: Date = Date(),
)
