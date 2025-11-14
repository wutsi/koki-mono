package com.wutsi.koki.lead.dto

import java.util.Date

data class Lead(
    val id: Long = -1,
    val listingId: Long = -1,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val message: String? = null,
    val requestVisitAt: Date? = null,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
