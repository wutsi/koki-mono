package com.wutsi.koki.portal.listing.model

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.refdata.dto.IDType
import java.util.Date

data class ListingModel(
    val heroImageUrl: String? = null,
    val id: Long = -1,
    val status: ListingStatus = ListingStatus.UNKNOWN,
    val listingNumber: Long = -1,
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
    val amenities: List<AmenityModel> = mutableListOf<AmenityModel>(),
    val address: AddressModel? = null,
    val geoLocation: GeoLocationModel? = null,
    val agentRemarks: String? = null,
    val publicRemarks: String? = null,
    val price: MoneyModel? = null,
    var visitFees: MoneyModel? = null,
    val leaseTerm: Int? = null,
    val securityDeposit: MoneyModel? = null,
    var advanceRent: Int? = null,
    var noticePeriod: Int? = null,
    val sellerName: String? = null,
    val sellerEmail: String? = null,
    val sellerPhone: String? = null,
    var sellerIdNumber: String? = null,
    var sellerIdType: IDType? = null,
    var sellerIdCountry: String? = null,
    val sellerAgentCommission: Double? = null,
    val buyerAgentCommission: Double? = null,
    val description: String? = null,
    val totalImages: Long? = null,
    val totalFiles: Long? = null,
    val published: Date? = null,
    val daysInMarket: Int? = null,
    val publicUrl: String? = null,
    val totalActiveMessages: Int? = null,
    val sellerAgentUser: UserModel? = null,
    val createdBy: UserModel? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var publishedAt: Date? = null,
    var closedAt: Date? = null,
) {
    val geoLocationUrl: String?
        get() = geoLocation?.let { geo ->
            "https://www.google.com/maps/dir/?api=1&destination=${geo.longitude}%2c${geo.latitude}"
        }

    val readOnly: Boolean
        get() = statusOffMarket

    val statusDraft: Boolean
        get() = status == ListingStatus.DRAFT

    val statusOnMarket: Boolean
        get() = status == ListingStatus.ACTIVE ||
            status == ListingStatus.ACTIVE_WITH_OFFER ||
            status == ListingStatus.PENDING

    val statusSuccessfulTransaction: Boolean
        get() = status == ListingStatus.SOLD ||
            status == ListingStatus.RENTED

    val statusOffMarket: Boolean
        get() = status == ListingStatus.SOLD ||
            status == ListingStatus.RENTED ||
            status == ListingStatus.EXPIRED ||
            status == ListingStatus.WITHDRAWN ||
            status == ListingStatus.CANCELLED

    val listingTypeRental: Boolean
        get() = listingType == ListingType.RENTAL

    fun amenitiesByCategoryId(categoryId: Long): List<AmenityModel> {
        return amenities.filter { amenity -> amenity.categoryId == categoryId }
    }

    fun canManage(user: UserModel?): Boolean {
        /**
         * - User can manage AND user is the seller agent
         * - user has full access
         */
        return ((user?.canManage("listing") == true) && (user.id == sellerAgentUser?.id)) ||
            (user?.hasFullAccess("listing") == true)
    }

    fun canAccess(user: UserModel?): Boolean {
        /**
         * - User can access AND user is the seller agent or listing is not draft
         * - user has full access
         */
        return ((user?.canAccess("listing") == true) && (user.id == sellerAgentUser?.id || !statusDraft)) ||
            (user?.hasFullAccess("listing") == true)
    }
}
