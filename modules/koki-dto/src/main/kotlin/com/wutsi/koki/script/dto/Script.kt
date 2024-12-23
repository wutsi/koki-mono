package com.wutsi.koki.script.dto

import java.util.Date

data class Script(
    val id: String = "",
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val language: Language = Language.UNKNOWN,
    val active: Boolean = true,
    val parameters: List<String> = emptyList(),
    val code: String = "",
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
)
