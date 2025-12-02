package com.wutsi.koki.lead.dto

import java.util.Date

data class Lead(
    val id: Long = -1,
    val listingId: Long = -1,
    val userId: Long = -1,
    val deviceId: String? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val source: LeadSource = LeadSource.UNKNOWN,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    var country: String? = null,
    val cityId: Long? = null,
    val message: String? = null,
    val visitRequestedAt: Date? = null,
    val nextContactAt: Date? = null,
    val nextVisitAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
