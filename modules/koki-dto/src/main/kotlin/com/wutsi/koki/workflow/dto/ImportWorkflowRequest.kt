package com.wutsi.koki.workflow.dto

data class ImportWorkflowRequest(
    val workflow: WorkflowData = WorkflowData()
)

data class ActivityData(
    val code: String = "",
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val description: String? = null,
    val requiresApproval: Boolean = true,
    val tags: Map<String, String> = emptyMap(),
    val predecessors: List<String> = emptyList()
)

data class WorkflowData(
    val name: String = "",
    val description: String = "",
    val activities: List<ActivityData> = emptyList(),
)
