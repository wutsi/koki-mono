package com.wutsi.koki.lead.dto.event

import com.wutsi.koki.lead.dto.LeadStatus

data class LeadStatusChangedEvent(
    val leadId: Long = -1,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val tenantId: Long = -1,
    val timestamp: Long = System.currentTimeMillis(),
)
