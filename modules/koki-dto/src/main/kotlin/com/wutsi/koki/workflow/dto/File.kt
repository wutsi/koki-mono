package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class File(
    val id: Long = -1,
    val memberId: Long? = null,
    val url: String = "",
    val contentType: String = "",
    val contentLength: Long = -1,
    val createdAt: Date = Date(),
    val createdByUserId: Long = -1,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val approvedAt: Date? = null,
    val approvedByUserId: Long? = null,
)
