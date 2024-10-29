package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Ticket(
    val id: Long = -1,
    val workflowId: Long = -1,
    val clientPartyId: Long = -1,
    val assigneeUserId: Long = -1,
    val approverUserId: Long? = null,
    val title: String = "",
    val status: TicketStatus = TicketStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val createdByUserId: Long = -1,
    val dueAt: Date? = null,
)
