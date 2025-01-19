package com.wutsi.koki.portal.user.model

import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class UserModel(
    val id: Long = -1,
    val email: String = "",
    val displayName: String = "",
    val status: UserStatus = UserStatus.ACTIVE,
    val roles: List<RoleModel> = emptyList(),
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
) {
    fun hasRole(roleId: Long): Boolean {
        return roles.find { role -> role.id == id } != null
    }
}
