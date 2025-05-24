package com.wutsi.koki.tenant.dto

import java.util.Date

data class User(
    val id: Long = -1,
    val accountId: Long? = null,
    var username: String = "",
    val email: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val type: UserType = UserType.UNKNOWN,
    val displayName: String = "",
    val language: String? = null,
    val roleIds: List<Long> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
