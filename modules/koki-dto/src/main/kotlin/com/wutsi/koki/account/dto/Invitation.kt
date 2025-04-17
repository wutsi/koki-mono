package com.wutsi.koki.account.dto

import java.util.Date

data class Invitation(
    val id: String = "",
    val accountId: Long = -1,
    val createdAt: Date = Date(),
    val createdById: Long? = null,
)
