package com.wutsi.koki.workflow.dto

data class SearchWorkflowResponse(
    val workflows: List<WorkflowSummary> = emptyList()
)
