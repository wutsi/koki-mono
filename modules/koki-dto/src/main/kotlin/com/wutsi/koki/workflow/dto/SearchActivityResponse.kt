package com.wutsi.koki.workflow.dto

data class SearchActivityResponse(
    val activities: List<ActivitySummary> = emptyList()
)
