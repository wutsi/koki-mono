package com.wutsi.koki.lead.dto

import java.util.Date

data class LeadSummary(
    val id: Long = -1,
    val listingId: Long = -1,
    val userId: Long = -1,
    val source: LeadSource = LeadSource.UNKNOWN,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val country: String? = null,
    val cityId: Long? = null,
    val visitRequestedAt: Date? = null,
    val nextContactAt: Date? = null,
    val nextVisitAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
