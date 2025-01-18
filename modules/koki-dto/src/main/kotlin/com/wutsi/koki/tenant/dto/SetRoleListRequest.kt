package com.wutsi.koki.tenant.dto

data class SetRoleListRequest(
    val roleIds: List<Long> = emptyList()
)
