package com.wutsi.koki.lead.dto

import java.util.Date

data class Lead(
    val id: Long = -1,
    val agentUserId: Long = -1,
    val lastMessageId: Long = -1,
    val listingId: Long? = null,
    val userId: Long = -1,
    val deviceId: String? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val source: LeadSource = LeadSource.UNKNOWN,
    val nextContactAt: Date? = null,
    val nextVisitAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val totalMessages: Int? = null,
)
