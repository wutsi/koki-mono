package com.wutsi.koki.script.dto

data class ExecuteScriptResponse(
    val bindings: Map<String, Any> = emptyMap(),
    val console: String = "",
)
