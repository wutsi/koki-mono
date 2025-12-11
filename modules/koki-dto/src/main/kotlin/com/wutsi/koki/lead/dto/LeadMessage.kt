package com.wutsi.koki.lead.dto

import java.util.Date

data class LeadMessage(
    val id: Long = -1,
    val leadId: Long = -1,
    val content: String = "",
    val visitRequestedAt: Date? = null,
    val createdAt: Date = Date(),
)
