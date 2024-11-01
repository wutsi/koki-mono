package com.wutsi.koki.workflow.dto

data class ImportJSONWorkflowRequest(
    val workflow: JSONWorkflow = JSONWorkflow()
)

data class JSONActivity(
    val code: String = "",
    val type: ActivityType = ActivityType.UNKNOWN,
    val name: String = "",
    val description: String? = null,
    val requiresApproval: Boolean = true,
    val tags: Map<String, String> = emptyMap(),
    val precedents: List<String> = emptyList()
)

data class JSONWorkflow(
    val name: String = "",
    val description: String = "",
    val activities: List<JSONActivity> = emptyList(),
)
