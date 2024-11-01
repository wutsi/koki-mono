package com.wutsi.koki.workflow.dto

data class ImportJSONWorkflowResponse(
    val added: Int = 0,
    val updated: Int = 0,
    val deactivated: Int = 0,
)
