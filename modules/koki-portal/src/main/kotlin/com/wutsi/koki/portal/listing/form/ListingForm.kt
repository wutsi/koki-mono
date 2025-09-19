package com.wutsi.koki.portal.listing.form

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import java.time.LocalDate

data class ListingForm(
    val id: Long = -1,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val listingNumber: Long? = null,
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
    val visitFees: Long? = null,
    val leaseTerm: Int? = null,
    val noticePeriod: Int? = null,
    val advanceRent: Int? = null,
    val securityDeposit: Long? = null,
    val currency: String? = null,
    val sellerContactId: Long? = null,
    val sellerAgentCommission: Double? = null,
    val buyerAgentCommission: Double? = null,
    val contractStartDate: LocalDate? = null,
    val contractEndDate: LocalDate? = null,
    val contractRemarks: String? = null,
    val buyerAgentUserId: Long? = null,
    val transactionDate: String? = null,
    val transactionPrice: Long? = null,
    val comment: String? = null,
)
