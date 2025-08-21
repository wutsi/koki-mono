package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ChangeListingStatusRequest(
    @get:NotNull val status: ListingStatus? = null,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val buyerPhone: String? = null,
    val buyerAgentUserId: Long? = null,
    val transactionDate: LocalDate? = null,
    val transactionRemarks: String? = null,
    val comment: String? = null,
)
