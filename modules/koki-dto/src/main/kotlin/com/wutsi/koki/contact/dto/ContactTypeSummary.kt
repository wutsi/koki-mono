package com.wutsi.koki.contact.dto

import java.util.Date

data class ContactTypeSummary(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
