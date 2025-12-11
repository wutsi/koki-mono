package com.wutsi.koki.lead.dto

import java.util.Date

data class LeadMessageSummary(
    val id: Long = -1,
    val leadId: Long = -1,
    val content: String = "",
    val visitRequestedAt: Date? = null,
    val createdAt: Date = Date(),
)
