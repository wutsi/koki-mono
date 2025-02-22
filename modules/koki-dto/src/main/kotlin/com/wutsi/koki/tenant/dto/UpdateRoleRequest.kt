package com.wutsi.koki.tenant.dto

data class UpdateRoleRequest(
    val name: String = "",
    val title: String? = null,
    val active: Boolean = true,
    val description: String? = null,
    val permissionIds: List<Long> = emptyList()
)
