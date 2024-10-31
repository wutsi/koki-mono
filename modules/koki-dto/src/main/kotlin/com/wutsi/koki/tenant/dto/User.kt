package com.wutsi.koki.tenant.dto

import java.util.Date

data class User(
    val id: Long = -1,
    val email: String = "",
    val status: UserStatus = UserStatus.UNKNOWN,
    val displayName: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val roles: List<Role> = emptyList(),
)
