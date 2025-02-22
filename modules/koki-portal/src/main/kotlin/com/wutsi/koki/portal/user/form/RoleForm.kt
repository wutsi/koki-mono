package com.wutsi.koki.portal.user.model

data class RoleForm(
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val active: Boolean = true,
    val permissionIds: List<Long> = emptyList(),
)
