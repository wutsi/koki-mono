package com.wutsi.koki.workflow.dto

data class ImportWorkflowRequest(
    val workflow: WorkflowData = WorkflowData()
)

data class ActivityData(
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val requiresApproval: Boolean = false,
    val role: String? = null,
    val tags: Map<String, String> = emptyMap(),
)

data class WorkflowData(
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val parameters: List<String> = emptyList(),
    val activities: List<ActivityData> = emptyList(),
    val flows: List<FlowData> = emptyList(),
)

data class FlowData(
    val from: String,
    val to: String,
    val expression: String? = null,
)
