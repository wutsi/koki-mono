package com.wutsi.koki.tenant.dto

import java.util.Date

data class UserSummary(
    val id: Long = -1,
    val email: String = "",
    val status: UserStatus = UserStatus.NEW,
    val displayName: String = "",
    val type: UserType = UserType.UNKNOWN,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
