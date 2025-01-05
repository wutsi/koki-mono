package com.wutsi.koki.account.dto

import java.util.Date

data class AccountSummary(
    val id: Long = -1,
    val accountTypeId: Long? = null,
    val name: String = "",
    val phone: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
    val managedById: Long? = null,
)
