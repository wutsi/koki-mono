package com.wutsi.koki.workflow.dto

data class ImportWorkflowRequest(
    val workflow: WorkflowData = WorkflowData()
)

data class ActivityData(
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val description: String? = null,
    val requiresApproval: Boolean = true,
    val tags: Map<String, String> = emptyMap(),
    val predecessors: List<String> = emptyList(),
    val role: String? = null,
)

data class WorkflowData(
    val name: String = "",
    val description: String? = null,
    val activities: List<ActivityData> = emptyList(),
)
