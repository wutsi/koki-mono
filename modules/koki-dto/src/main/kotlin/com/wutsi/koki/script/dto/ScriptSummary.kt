package com.wutsi.koki.script.dto

import java.util.Date

data class ScriptSummary(
    val id: String = "",
    val name: String = "",
    val title: String? = null,
    val language: Language = Language.UNKNOWN,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
