package com.wutsi.koki.listing.server.domain

import com.wutsi.koki.listing.dto.ListingStatus
import java.util.Date

data class FeedListing(
    val id: Long,
    val url: String = "",
    val urlHash: String = "",
    val content: String = "",
    val imageUrls: String = "",
    val status: ListingStatus = ListingStatus.DRAFT,
    val createdAt: Date = Date(),
)
