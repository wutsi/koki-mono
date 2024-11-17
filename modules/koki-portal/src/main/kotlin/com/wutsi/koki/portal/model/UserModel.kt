package com.wutsi.koki.portal.model

import com.wutsi.koki.tenant.dto.UserStatus

data class UserModel(
    val id: Long = -1,
    val email: String = "",
    val displayName: String = "",
    val status: UserStatus = UserStatus.UNKNOWN,
    val roles: List<RoleModel> = emptyList()
)
