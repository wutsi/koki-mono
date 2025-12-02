package com.wutsi.koki.lead.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.Date

data class CreateLeadRequest(
    val listingId: Long = -1,
    val userId: Long? = null,

    @get:NotEmpty @get:Size(max = 50) val firstName: String = "",
    @get:NotEmpty @get:Size(max = 50) val lastName: String = "",
    @get:NotEmpty @get:Size(max = 100) val email: String = "",
    @get:NotEmpty @get:Size(max = 30) val phoneNumber: String = "",
    @get:Size(max = 2) val country: String? = null,

    val message: String? = null,
    val visitRequestedAt: Date? = null,
    val source: LeadSource = LeadSource.UNKNOWN,
    val cityId: Long? = null,
)
