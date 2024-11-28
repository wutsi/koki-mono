package com.wutsi.koki.portal.model

import java.util.Date

data class FormModel(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val content: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val viewUrl: String = "",
    val editUrl: String = "",
) {
    val longTitle: String
        get() = if (title.isEmpty()) {
            name
        } else {
            "$name - $title"
        }
}
