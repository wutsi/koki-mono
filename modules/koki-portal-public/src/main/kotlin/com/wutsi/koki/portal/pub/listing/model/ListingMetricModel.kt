package com.wutsi.koki.portal.pub.listing.model

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.portal.pub.common.model.MoneyModel
import com.wutsi.koki.sdk.URLBuilder

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
) {
    val priceRangeText: String
        get() = if (minPrice.amount == maxPrice.amount) {
            minPrice.shortText
        } else {
            val parts = maxPrice.shortText.split(' ') // Currency and amount
            "${minPrice.shortText} - ${parts[1]}"
        }

    val searchUrl: String
        get() {
            val urlBuilder = URLBuilder("")
            return urlBuilder.build(
                "/search",
                mapOf(
                    "location-id" to (neighborhoodId ?: cityId),
                    "listing-type" to listingType.takeIf { it != ListingType.UNKNOWN },
                    "property-category" to propertyCategory.takeIf { it != PropertyCategory.UNKNOWN },
                    "bedrooms" to bedrooms
                        ?.takeIf { propertyCategory == PropertyCategory.RESIDENTIAL }
                        ?.let { rooms -> if (rooms > 5) "5+" else rooms },
                )
            )
        }
}
