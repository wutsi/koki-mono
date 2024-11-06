package com.wutsi.koki.workflow.dto

data class SearchWorkflowInstanceResponse(
    val workflowInstances: List<WorkflowInstanceSummary> = emptyList()
)
