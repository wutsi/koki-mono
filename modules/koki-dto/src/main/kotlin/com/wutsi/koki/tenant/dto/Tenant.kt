package com.wutsi.koki.tenant.dto

import java.util.Date

data class Tenant(
    val id: Long = -1,
    val name: String = "",
    val domainName: String = "",
    val currency: String = "",
    val locale: String = "",
    val status: TenantStatus = TenantStatus.UNKNOWN,
    val createdAt: Date = Date(),
)
