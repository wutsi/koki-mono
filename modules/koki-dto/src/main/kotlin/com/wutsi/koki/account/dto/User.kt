package com.wutsi.koki.account.dto

import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class User(
    val username: String = "",
    val status: UserStatus = UserStatus.NEW,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
