package com.wutsi.koki.portal.model

import com.wutsi.koki.script.dto.Language
import java.util.Date

data class ScriptModel(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val language: Language = Language.UNKNOWN,
    val active: Boolean = true,
    val parameters: List<String> = emptyList(),
    val code: String = "",
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }

    val url: String
        get() = "/settings/scripts/$id"

    val parameterText: String
        get() = parameters.joinToString(separator = "\n")
}
