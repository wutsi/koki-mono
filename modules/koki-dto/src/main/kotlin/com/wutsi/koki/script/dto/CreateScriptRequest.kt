package com.wutsi.koki.script.dto

import jakarta.validation.constraints.NotEmpty

data class CreateScriptRequest(
    @get:NotEmpty val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val language: Language = Language.UNKNOWN,
    val active: Boolean = true,
    val parameters: List<String> = emptyList(),
    @get:NotEmpty val code: String = "",
)
