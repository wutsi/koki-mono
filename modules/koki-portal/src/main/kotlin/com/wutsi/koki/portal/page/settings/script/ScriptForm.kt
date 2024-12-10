package com.wutsi.koki.portal.script

data class ScriptForm(
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val language: String = "",
    val active: Boolean = true,
    val parameters: String = "",
    val code: String = "",
)
