package com.wutsi.koki.tenant.dto

import java.util.Date

data class TenantUser(
    val id: Long = -1,
    val user: User = User(),
    val role: TenantRole = TenantRole(),
    val createdAt: Date = Date(),
    val lastLoginAt: Date = Date(),
)
