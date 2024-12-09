package com.wutsi.koki.script.dto

data class SearchScriptResponse(
    val scripts: List<ScriptSummary> = emptyList()
)
