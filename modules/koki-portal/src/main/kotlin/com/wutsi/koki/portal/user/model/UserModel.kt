package com.wutsi.koki.portal.user.model

import com.wutsi.koki.portal.module.model.ModuleModel
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
    val permissionNames: List<String> = emptyList(),
) {
    fun hasRole(roleId: Long): Boolean {
        return roles.find { role -> role.id == roleId } != null
    }

    fun hasPermission(name: String): Boolean {
        return permissionNames.contains(name)
    }

    fun canAccess(module: ModuleModel): Boolean {
        return hasPermission(module.name)
    }

    fun canAdmin(module: ModuleModel): Boolean {
        return hasPermission("${module.name}:admin")
    }
}
