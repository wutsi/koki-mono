package com.wutsi.koki.portal.model

import java.util.Date

data class MessageModel(
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val body: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAtText: String = "",
) {
    val url: String
        get() = "/messages/$id"

    val editUrl: String
        get() = "/messages/$id/edit"
}
