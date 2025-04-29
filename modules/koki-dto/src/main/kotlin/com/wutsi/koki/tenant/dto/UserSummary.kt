package com.wutsi.koki.tenant.dto

import java.util.Date

data class UserSummary(
    val id: Long = -1,
    var username: String = "",
    val email: String = "",
    val status: UserStatus = UserStatus.NEW,
    val type: UserType = UserType.UNKNOWN,
    val displayName: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
