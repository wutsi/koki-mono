package com.wutsi.koki.portal.user.model

data class RoleForm(
    val name: String = "",
    val title: String = "",
    val description: String? = null,
    val active: Boolean = true,
)
