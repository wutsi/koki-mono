package com.wutsi.koki.tenant.dto

import java.util.Date

data class User(
    val id: Long = -1,
    val status: UserStatus = UserStatus.NEW,
    var username: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val language: String? = null,
    val employer: String? = null,
    val mobile: String? = null,
    val categoryId: Long? = null,
    val cityId: Long? = null,
    val country: String? = null,
    val photoUrl: String? = null,
    val roleIds: List<Long> = emptyList(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
