package com.wutsi.koki.tenant.dto

import java.util.Date

data class User(
    val id: Long = -1,
    val email: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val displayName: String = "",
    val language: String? = null,
    val type: UserType = UserType.UNKNOWN,
    val roleIds: List<Long> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
