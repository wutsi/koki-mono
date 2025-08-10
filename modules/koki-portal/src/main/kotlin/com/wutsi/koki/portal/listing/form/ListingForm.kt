package com.wutsi.koki.portal.listing.form

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import java.time.LocalDate

data class ListingForm(
    val id: Long = -1,
    val listingType: ListingType? = null,
    val propertyType: PropertyType? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val halfBathrooms: Int? = null,
    val floors: Int? = null,
    val basementType: BasementType? = null,
    val level: Int? = null,
    val unit: String? = null,
    val parkingType: ParkingType? = null,
    val parkings: Int? = null,
    val fenceType: FenceType? = null,
    val lotArea: Int? = null,
    val propertyArea: Int? = null,
    val year: Int? = null,
    val furnitureType: FurnitureType? = null,
    val amenityIds: List<Long> = mutableListOf<Long>(),
    val country: String? = null,
    val cityId: Long? = null,
    val neighbourhoodId: Long? = null,
    val street: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val agentRemarks: String? = null,
    val publicRemarks: String? = null,
    val price: Long? = null,
    val securityDeposit: Long? = null,
    val leaseTerm: Int? = null,
    val currency: String? = null,
    val sellerName: String? = null,
    val sellerEmail: String? = null,
    val sellerPhone: String? = null,
    val sellerAgentCommission: Int? = null,
    val buyerAgentCommission: Int? = null,
    val contractStartDate: LocalDate? = null,
    val contractEndDate: LocalDate? = null,
    val contractRemarks: String? = null,
)
