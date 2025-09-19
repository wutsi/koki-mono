package com.wutsi.koki.listing.dto

data class CloseListingRequest(
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val comment: String? = null,
)
