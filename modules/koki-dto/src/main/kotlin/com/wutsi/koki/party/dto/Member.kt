package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Member(
    val id: Long = -1,
    val partyId: Long = -1,
    val parentPartyId: Long = -1,
    val role: MemberRole = MemberRole(),
    val memberAttributes: List<MemberAttribute> = emptyList(),
    val createdAt: Date = Date(),
)
