package com.wutsi.koki.script.dto

data class Script(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val language: Language = Language.UNKNOWN,
    val active: Boolean = true,
    val parameters: List<String>,
    val code: String = "",
)
