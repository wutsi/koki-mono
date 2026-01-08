package com.wutsi.koki.listing.dto

import java.util.Date

data class ListingMetricSummary(
    val neighborhoodId: Long? = null,
    val sellerAgentUserId: Long? = null,
    val cityId: Long? = null,
    val bedrooms: Int? = null,

    val propertyCategory: PropertyCategory = PropertyCategory.UNKNOWN,
    val listingStatus: ListingStatus = ListingStatus.UNKNOWN,
    val listingType: ListingType = ListingType.UNKNOWN,
    val total: Long = 0,
    val minPrice: Long = 0L,
    val maxPrice: Long = 0L,
    val averagePrice: Long = 0L,
    val averageLotArea: Int? = null,
    val pricePerSquareMeter: Long? = null,
    val totalPrice: Long = 0L,
    val currency: String? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
