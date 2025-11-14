package com.wutsi.koki.lead.dto.event

data class LeadCreatedEvent (
    val leadId: Long = -1,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
