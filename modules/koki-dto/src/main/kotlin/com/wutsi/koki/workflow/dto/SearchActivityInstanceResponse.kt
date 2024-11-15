package com.wutsi.koki.workflow.dto

data class SearchActivityInstanceResponse(
    val activityInstances: List<ActivityInstanceSummary> = emptyList()
)
