package com.wutsi.koki.workflow.dto

data class SearchLogEntryResponse(
    val logEntries: List<LogEntrySummary> = emptyList()
)
