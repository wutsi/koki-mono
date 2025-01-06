package com.wutsi.koki.contact.dto

import java.util.Date

data class ContactType(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val description: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
