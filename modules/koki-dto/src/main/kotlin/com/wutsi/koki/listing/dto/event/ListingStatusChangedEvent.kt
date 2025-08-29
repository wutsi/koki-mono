package com.wutsi.koki.listing.dto.event

import com.wutsi.koki.listing.dto.ListingStatus

data class ListingStatusChangedEvent(
    val listingId: Long = -1,
    val tenantId: Long = -1,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis()
)
