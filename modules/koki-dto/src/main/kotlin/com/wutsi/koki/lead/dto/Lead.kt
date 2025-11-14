package com.wutsi.koki.lead.dto

import java.util.Date

data class CreateLeadRequest(
    val listingId: Long = -1,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val message: String? = null,
    val visitDate: Date? = null,
)
