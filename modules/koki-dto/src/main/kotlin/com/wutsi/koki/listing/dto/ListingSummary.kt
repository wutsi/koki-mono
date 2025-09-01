package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class ListingSummary(
    val id: Long = -1,
    val heroImageId: Long? = null,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val listingNumber: Long = -1,
    val listingType: ListingType? = null,
    val propertyType: PropertyType? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val halfBathrooms: Int? = null,
    val lotArea: Int? = null,
    val propertyArea: Int? = null,
    val furnitureType: FurnitureType? = null,
    val address: Address? = null,
    val price: Money? = null,
    val buyerAgentCommission: Double? = null,
    val sellerAgentUserId: Long? = null,
    val buyerAgentUserId: Long? = null,
    val transactionDate: Date? = null,
    val transactionPrice: Money? = null,
)
