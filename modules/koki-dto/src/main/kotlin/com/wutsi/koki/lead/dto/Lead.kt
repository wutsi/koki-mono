package com.wutsi.koki.lead.dto

import java.util.Date

data class Lead(
    val id: Long = -1,
    val listingId: Long? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val source: LeadSource = LeadSource.UNKNOWN,
    val firstName: String = "",
    val lastName: String = "",
    val email: String? = null,
    val phoneNumber: String = "",
    val message: String? = null,
    val visitRequestedAt: Date? = null,
    val nextContactAt: Date? = null,
    val nextMeetingAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
