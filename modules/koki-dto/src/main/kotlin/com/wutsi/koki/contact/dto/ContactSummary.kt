package com.wutsi.koki.contact.dto

import java.util.Date

data class ContactSummary(
    val id: Long = -1,
    val accountId: Long? = null,
    val contactTypeId: Long? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
