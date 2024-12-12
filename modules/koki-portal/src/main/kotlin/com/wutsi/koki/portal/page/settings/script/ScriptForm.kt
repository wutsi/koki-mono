package com.wutsi.koki.portal.script

import com.wutsi.koki.script.dto.Language

data class ScriptForm(
    val name: String = "",
    val title: String = "",
    val description: String = "",
    val language: String = Language.JAVASCRIPT.name.lowercase(),
    val active: Boolean = true,
    val parameters: String = "",
    val code: String = "",
)
