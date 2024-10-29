package org.example.com.wutsi.koki.tenant.dto

import java.util.Date

data class Task(
    val id: Long = -1,
    val activityId: Long = -1,
    val ticketId: Long = -1,
    val assigneeUserId: Long = -1,
    val status: TicketStatus = TicketStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val dueAt: Date? = null,
    val approval: ApprovalStatus = ApprovalStatus.UNKNOWN,
    val approvedAt: Date? = null,
    val taskAttributes: List<AttributeNVP> = emptyList(),
    val inputDocumentFiles: List<DocumentFile> = emptyList(),
    val outputFiles: List<File> = emptyList(),
)
