package com.wutsi.koki.workflow.dto

import java.util.Date

data class TicketFile(
    val id: Long = -1,
    val folder: String = "",
    val file: File = File(),
    val memberId: Long? = null,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val approvedAt: Date = Date(),
    val approvedByUserId: Long = -1,
)
