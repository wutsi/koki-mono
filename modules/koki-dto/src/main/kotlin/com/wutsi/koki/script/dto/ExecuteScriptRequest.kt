package com.wutsi.koki.script.dto

data class ExecuteScriptRequest(
    val parameters: Map<String, Any> = emptyMap()
)
