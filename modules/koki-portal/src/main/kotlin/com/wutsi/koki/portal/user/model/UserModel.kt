package com.wutsi.koki.portal.user.model

import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import java.util.Date

data class UserModel(
    val id: Long = -1,
    val username: String = "",
    val accountId: Long? = null,
    val email: String = "",
    val displayName: String = "",
    val employer: String? = null,
    val phone: String? = null,
    val photoUrl: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val type: UserType = UserType.UNKNOWN,
    val roles: List<RoleModel> = emptyList(),
    val language: String? = null,
    val languageText: String? = null,
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

    fun canAccess(module: String): Boolean {
        return permissionNames.contains(module)
    }

    fun hasFullAccess(module: String): Boolean {
        return permissionNames.contains("$module:full_access")
    }

    fun canManage(module: String): Boolean {
        return permissionNames.contains("$module:manage")
    }

    fun canDelete(module: String): Boolean {
        return permissionNames.contains("$module:delete")
    }

    fun canAccess(module: ModuleModel): Boolean {
        return hasPermission(module.name) || hasFullAccess(module.name)
    }

    fun canAdmin(module: ModuleModel): Boolean {
        return hasPermission("${module.name}:admin")
    }
}
