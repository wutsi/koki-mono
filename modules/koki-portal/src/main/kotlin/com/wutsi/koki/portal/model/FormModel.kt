package com.wutsi.koki.portal.model

import java.util.Date

data class FormModel(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val content: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val viewUrl: String = "",
    val editUrl: String = "",
    val shareUrl: String = "",
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }

    val url: String
        get() = "/settings/forms/$id"
}
