package com.wutsi.koki.portal.listing.form

import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType

data class ListingFilterForm(
    val listingNumber: String? = null,
    val locationId: Long? = null,
    val listingType: ListingType? = null,
    val propertyType: PropertyType? = null,
    val bedrooms: String? = null,
    val bathrooms: String? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val minLotArea: Int? = null,
    val maxLotArea: Int? = null,
    val minPropertyArea: Int? = null,
    val maxPropertyArea: Int? = null,
    val sellerAgentUserId: Long? = null,
) {
    fun isEmpty(): Boolean {
        return listingNumber.isNullOrEmpty() &&
            locationId == null &&
            listingType == null &&
            propertyType == null &&
            bathrooms.isNullOrEmpty() &&
            bedrooms.isNullOrEmpty() &&
            minPrice == null &&
            minLotArea == null &&
            maxLotArea == null &&
            minPropertyArea == null &&
            maxPropertyArea == null &&
            sellerAgentUserId == null
    }
}
