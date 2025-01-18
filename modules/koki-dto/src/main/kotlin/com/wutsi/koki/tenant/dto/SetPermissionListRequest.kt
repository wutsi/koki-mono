package com.wutsi.koki.tenant.dto

data class SetPermissionListRequest(
    val permissionIds: List<Long> = emptyList()
)
