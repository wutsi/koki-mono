package com.wutsi.koki.portal.user.model

import com.wutsi.koki.portal.module.model.PermissionModel
import java.util.Date

data class RoleModel(
    val id: Long = -1,
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val createdAtText: String = "",
    val modifiedAt: Date = Date(),
    val modifiedAtText: String = "",
    val permissions: List<PermissionModel> = emptyList(),
) {
    fun hasPermissions(id: Long): Boolean {
        return permissions.find { permission -> permission.id == id } != null
    }

    fun hasPermissions(name: String): Boolean {
        return permissions.find { permission -> permission.name.equals(name, true) } != null
    }
}
