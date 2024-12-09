package com.wutsi.koki.script.dto

data class ScriptSummary(
    val id: String = "",
    val name: String = "",
    val language: Language = Language.UNKNOWN,
    val active: Boolean = true,
)
