package com.wutsi.koki.agent.dto

import com.wutsi.koki.listing.dto.ListingType
import java.util.Date

data class AgentMetric(
    val id: Long = -1,
    val listingType: ListingType = ListingType.UNKNOWN,
    val period: MetricPeriod = MetricPeriod.UNKNOWN,
    val total: Long = 0,
    val minPrice: Long = 0L,
    val maxPrice: Long = 0L,
    val averagePrice: Long = 0L,
    val totalPrice: Long = 0L,
    val currency: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
