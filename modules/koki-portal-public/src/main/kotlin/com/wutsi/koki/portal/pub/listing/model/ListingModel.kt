package com.wutsi.koki.portal.pub.listing.model

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.pub.common.model.MoneyModel
import com.wutsi.koki.portal.pub.file.model.FileModel
import com.wutsi.koki.portal.pub.refdata.model.AddressModel
import com.wutsi.koki.portal.pub.refdata.model.AmenityModel
import com.wutsi.koki.portal.pub.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.pub.user.model.UserModel
import java.util.Date

data class ListingModel(
    val heroImageUrl: String? = null,
    val id: Long = -1,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val listingNumber: String = "",
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
    var availableAt: Date? = null,
    var availableAtText: String? = null,
    var roadPavement: RoadPavement? = null,
    var distanceFromMainRoad: Int? = null,
    val furnitureType: FurnitureType? = null,
    val amenities: List<AmenityModel> = mutableListOf<AmenityModel>(),
    val address: AddressModel? = null,
    val geoLocation: GeoLocationModel? = null,
    val publicRemarks: String? = null,
    val price: MoneyModel? = null,
    var visitFees: MoneyModel? = null,
    val leaseTerm: Int? = null,
    val securityDeposit: MoneyModel? = null,
    var advanceRent: Int? = null,
    var advanceRentMoney: MoneyModel? = null,
    var noticePeriod: Int? = null,
    val sellerAgentCommission: Double? = null,
    val buyerAgentCommission: Double? = null,
    val buyerAgentUser: UserModel? = null,
    var transactionDate: Date? = null,
    var transactionDateText: String? = null,
    var transactionPrice: MoneyModel? = null,

    val title: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val publicUrl: String? = null,
    val published: Date? = null,
    val daysInMarket: Int? = null,
    val sellerAgentUser: UserModel? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var publishedAt: Date? = null,
    var publishedAtMoment: String? = null,
    var closedAt: Date? = null,
    var closedAtMoment: String? = null,

    val totalImages: Int? = null,
    val totalOffers: Int? = null,
    val totalFiles: Int? = null,
    val totalActiveMessages: Int? = null,

    val images: List<FileModel> = emptyList()
) {
    val descriptionHtml: String?
        get() = description?.let { str -> HtmlUtils.toHtml(str) }

    val publicRemarksHtml: String?
        get() = publicRemarks?.let { str -> HtmlUtils.toHtml(str) }

    val listingTypeRental: Boolean
        get() = listingType == ListingType.RENTAL

    val listingTypeSale: Boolean
        get() = listingType == ListingType.SALE

    val statusDraft: Boolean
        get() = status == ListingStatus.DRAFT

    val statusActive: Boolean
        get() = status == ListingStatus.ACTIVE || status == ListingStatus.ACTIVE_WITH_CONTINGENCIES

    val statusOnMarket: Boolean
        get() = status == ListingStatus.ACTIVE || status == ListingStatus.ACTIVE_WITH_CONTINGENCIES

    val statusSold: Boolean
        get() = status == ListingStatus.SOLD ||
            status == ListingStatus.RENTED

    fun amenitiesByCategoryId(categoryId: Long): List<AmenityModel> {
        return amenities.filter { amenity -> amenity.categoryId == categoryId }
    }
}
