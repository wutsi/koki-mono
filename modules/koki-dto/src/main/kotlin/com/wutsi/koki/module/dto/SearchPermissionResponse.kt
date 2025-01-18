package com.wutsi.koki.module.dto

data class SearchPermissionResponse(
    val permissions: List<Permission> = emptyList(),
)
