package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Party(
    val id: Long = -1,
    val tenantId: Long = -1,
    val userId: Long = -1,
    val name: String = "",
    val type: PartyType = PartyType.UNKNOWN,
    val createdAt: Date = Date(),
)
