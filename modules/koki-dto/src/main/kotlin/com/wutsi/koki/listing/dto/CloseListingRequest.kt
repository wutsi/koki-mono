package com.wutsi.koki.listing.dto

import java.util.Date

data class CloseListingRequest(
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val buyerName: String? = null,
    val buyerEmail: String? = null,
    val buyerPhone: String? = null,
    val buyerAgentUserId: Long? = null,
    val transactionPrice: Long? = null,
    val transactionDate: Date? = null,
    val comment: String? = null,
)
