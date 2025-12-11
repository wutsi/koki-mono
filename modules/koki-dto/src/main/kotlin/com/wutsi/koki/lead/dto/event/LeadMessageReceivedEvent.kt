package com.wutsi.koki.lead.dto.event

data class LeadMessageReceivedEvent(
    val messageId: Long = -1,
    val newLead: Boolean = false,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
