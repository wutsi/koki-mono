package com.wutsi.koki.listing.dto

import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class Listing(
    val id: Long = -1,
    val heroImageId: Long? = null,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    @Deprecated("") val listingNumber: Long = -1,
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
    val availableAt: Date? = null,
    val roadPavement: RoadPavement? = null,
    val distanceFromMainRoad: Int? = null,

    val furnitureType: FurnitureType? = null,
    val amenityIds: List<Long> = emptyList(),

    val address: Address? = null,

    val geoLocation: GeoLocation? = null,

    val price: Money? = null,
    val visitFees: Money? = null,
    val sellerAgentCommission: Double? = null,
    val buyerAgentCommission: Double? = null,
    val sellerAgentCommissionMoney: Money? = null,
    val buyerAgentCommissionMoney: Money? = null,

    val units: Int? = null,
    val revenue: Money? = null,

    val securityDeposit: Int? = null,
    val advanceRent: Int? = null,
    val leaseTerm: Int? = null,
    val noticePeriod: Int? = null,

    val sellerContactId: Long? = null,

    val agentRemarks: String? = null,
    val publicRemarks: String? = null,

    val buyerAgentUserId: Long? = null,
    val buyerContactId: Long? = null,
    val closedOfferId: Long? = null,
    var soldAt: Date? = null,
    var salePrice: Money? = null,

    var title: String? = null,
    var summary: String? = null,
    var description: String? = null,
    var titleFr: String? = null,
    var summaryFr: String? = null,
    var descriptionFr: String? = null,
    val totalImages: Int? = null,
    val totalFiles: Int? = null,
    val totalLeads: Int? = null,
    val sellerAgentUserId: Long? = null,

    // Legal information
    val landTitle: Boolean? = null,
    val technicalFile: Boolean? = null,
    val numberOfSigners: Int? = null,
    val mutationType: MutationType? = null,
    val transactionWithNotary: Boolean? = null,
    val subdivided: Boolean? = null,
    val morcelable: Boolean? = null,

    val createdById: Long? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val publishedAt: Date? = null,
    val closedAt: Date? = null,
    val finalSellerAgentCommissionMoney: Money? = null,
    val finalBuyerAgentCommissionMoney: Money? = null,
    val publicUrl: String? = null,
    val publicUrlFr: String? = null,
    val qrCodeUrl: String? = null,
)
