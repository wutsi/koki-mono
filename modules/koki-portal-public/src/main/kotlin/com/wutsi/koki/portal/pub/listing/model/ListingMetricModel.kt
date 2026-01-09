package com.wutsi.koki.portal.pub.listing.model

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.portal.pub.common.model.MoneyModel

data class ListingMetricModel(
    val neighborhoodId: Long? = null,
    val sellerAgentUserId: Long? = null,
    val cityId: Long? = null,
    val bedrooms: Int? = null,

    val propertyCategory: PropertyCategory = PropertyCategory.UNKNOWN,
    val listingStatus: ListingStatus = ListingStatus.UNKNOWN,
    val listingType: ListingType = ListingType.UNKNOWN,
    val total: Long = 0,
    val minPrice: MoneyModel = MoneyModel(),
    val maxPrice: MoneyModel = MoneyModel(),
    val averagePrice: MoneyModel = MoneyModel(),
    val averageLotArea: Int? = null,
    val pricePerSquareMeter: MoneyModel? = null,
    val totalPrice: MoneyModel = MoneyModel(),
)
