package com.wutsi.koki.listing.dto

import java.util.Date

data class CloseListingRequest(
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val comment: String? = null,
    val salePrice: Long? = null,
    val soldAt: Date? = null,
    val closedOfferId: Long? = null,
    val buyerContactId: Long? = null,
    val buyerAgentUserId: Long? = null,
)
