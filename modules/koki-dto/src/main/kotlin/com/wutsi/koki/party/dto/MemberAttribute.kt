package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class MemberAttribute(
    val id: Long = -1,
    val memberId: Long = -1,
    val attribute: Attribute = Attribute(),
    val value: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
