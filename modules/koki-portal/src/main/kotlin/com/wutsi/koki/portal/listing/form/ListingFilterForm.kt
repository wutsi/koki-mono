package com.wutsi.koki.portal.listing.form

import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.platform.util.NumberUtils

data class ListingFilterForm(
    val listingNumber: String? = null,
    val locationIds: List<Long> = emptyList(),
    val listingType: String = ListingType.RENTAL.name,
    val propertyTypes: List<PropertyType> = emptyList(),
    val bedrooms: String = "",
    val bathrooms: String = "",
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val minLotArea: Int? = null,
    val maxLotArea: Int? = null,
    val minPropertyArea: Int? = null,
    val maxPropertyArea: Int? = null,
    val sellerAgentUserId: Long? = null,
    val submitted: Boolean = false,
) {
    fun containsPropertyType(type: PropertyType): Boolean {
        return propertyTypes.isNotEmpty() && propertyTypes.contains(type)
    }

    val minPriceText: String?
        get() = minPrice?.let { price -> NumberUtils.shortText(price) }

    val maxPriceText: String?
        get() = maxPrice?.let { price -> NumberUtils.shortText(price) }

    val minLotAreaText: String?
        get() = minLotArea?.let { value -> NumberUtils.shortText(value.toLong()) }

    val maxLotAreaText: String?
        get() = maxLotArea?.let { value -> NumberUtils.shortText(value.toLong()) }

    val minPropertyAreaText: String?
        get() = minPropertyArea?.let { value -> NumberUtils.shortText(value.toLong()) }

    val maxPropertyAreaText: String?
        get() = maxPropertyArea?.let { value -> NumberUtils.shortText(value.toLong()) }
}
