package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.Address
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.Date

data class CreateListingRequest(
    @get:NotNull val listingType: ListingType? = null,
    @get:NotNull val propertyType: PropertyType? = null,
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
    val availableAt: Date? = null,
    val roadPavement: RoadPavement? = null,
    val distanceFromMainRoad: Int? = null,

    @get:Valid val address: Address? = null,

    val price: Long? = null,
    val visitFees: Long? = null,
    val revenue: Long? = null,
    @get:Size(max = 3) val currency: String? = null,
    var sellerAgentCommission: Double? = null,
    var buyerAgentCommission: Double? = null,
    val units: Int? = null,
    val leaseTerm: Int? = null,
    val noticePeriod: Int? = null,
    val advanceRent: Int? = null,
    val securityDeposit: Int? = null,

    val publicRemarks: String? = null,

    val furnitureType: FurnitureType? = null,
    val amenityIds: List<Long> = emptyList(),

    val landTitle: Boolean? = null,
    val technicalFile: Boolean? = null,
    @get:Min(0) val numberOfSigners: Int? = null,
    val mutationType: MutationType? = null,
    val transactionWithNotary: Boolean? = null,
    val subdivided: Boolean? = null,
    val morcelable: Boolean? = null,
    val sellerAgentUserId: Long? = null,
)
