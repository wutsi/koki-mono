package com.wutsi.koki.account.dto

import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class AccountUser(
    val id: Long = -1,
    val accountId: Long = -1,
    val username: String = "",
    val status: UserStatus = UserStatus.NEW,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)
