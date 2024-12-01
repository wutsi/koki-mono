package com.wutsi.koki.message.dto

import java.util.Date

data class MessageSummary(
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
