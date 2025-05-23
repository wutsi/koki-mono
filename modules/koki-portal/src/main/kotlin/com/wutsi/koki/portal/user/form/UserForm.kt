package com.wutsi.koki.portal.user.model

import com.wutsi.koki.tenant.dto.UserStatus

data class UserForm(
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val password: String = "",
    val language: String? = null,
    val roleIds: List<Long> = emptyList(),
)
