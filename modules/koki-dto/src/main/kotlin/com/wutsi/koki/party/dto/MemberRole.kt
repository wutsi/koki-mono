package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class MemberRole(
    val id: Long = -1,
    val name: String = "",
    val createdAt: Date = Date(),
)
