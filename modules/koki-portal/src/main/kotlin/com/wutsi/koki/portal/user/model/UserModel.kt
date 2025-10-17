package com.wutsi.koki.portal.user.model

import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.module.model.ModuleModel
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.tenant.dto.UserStatus
import java.util.Date

data class UserModel(
    val id: Long = -1,
    val username: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val employer: String? = null,
    val mobile: String? = null,
    val mobileText: String? = null,
    val photoUrl: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val roles: List<RoleModel> = emptyList(),
    val language: String? = null,
    val languageText: String? = null,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val permissionNames: List<String> = emptyList(),
    val category: CategoryModel? = null,
    val city: LocationModel? = null,
    val country: String? = null,
    val whatsappUrl: String? = null,
    val biography: String? = null,
    val websiteUrl: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val twitterUrl: String? = null,
    val tiktokUrl: String? = null,
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

    fun canAdmin(): Boolean {
        return permissionNames.find { permission -> permission.endsWith(":admin") } != null
    }

    val mobileUrl: String?
        get() = mobile?.let { "tel:" + mobile }

    val biographyHtml: String?
        get() = biography?.let { bio -> HtmlUtils.toHtml(bio) }
}
