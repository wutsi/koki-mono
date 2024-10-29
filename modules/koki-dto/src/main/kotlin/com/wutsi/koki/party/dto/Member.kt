package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Member(
    val id: Long = -1,
    val partyId: Long = -1,
    val name: String = "",
    val createdAt: Date = Date(),
)
