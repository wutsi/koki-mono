package com.wutsi.koki.tenant.dto

import java.util.Date

data class TenantRole(
    val id: Long = -1,
    val name: String = "",
    val createdAt: Date = Date(),
)
