package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class TenantAttribute(
    val id: Long = -1,
    val tenantId: Long = -1,
    val attribute: Attribute = Attribute(),
    val value: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
