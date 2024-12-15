package com.wutsi.koki.script.dto

data class RunScriptResponse(
    val bindings: Map<String, Any> = emptyMap(),
    val console: String = "",
)
