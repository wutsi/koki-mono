package com.wutsi.koki.listing.dto

import java.time.LocalDate

data class UpdateListingStatusRequest(
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val buyerPhone: String? = null,
    val buyerAgentUserId: Long? = null,
    val transactionDate: LocalDate? = null,
    val transactionRemarks: String? = null,
    val comment: String? = null,
)
