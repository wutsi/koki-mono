package com.wutsi.koki.script.dto

data class RunScriptRequest(
    val language: Language = Language.UNKNOWN,
    val parameters: Map<String, Any> = emptyMap(),
    val code: String = "",
)
