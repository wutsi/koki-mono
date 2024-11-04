package com.wutsi.koki.workflow.dto

data class RunNextWorkflowInstanceResponse(
    val activityInstanceIds: List<String> = emptyList()
)
