package com.wutsi.koki.tenant.dto

import java.util.Date

data class UserSummary(
    val id: Long = -1,
    val status: UserStatus = UserStatus.NEW,
    var username: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
