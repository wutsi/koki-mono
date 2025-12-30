package com.wutsi.koki.portal.listing.model

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.MutationType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.platform.util.HtmlUtils
import com.wutsi.koki.portal.common.model.MoneyModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.user.model.UserModel
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
    val levelHtml: String? = null,
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
    val agentRemarks: String? = null,
    val publicRemarks: String? = null,
    val price: MoneyModel? = null,
    var visitFees: MoneyModel? = null,
    val leaseTerm: Int? = null,
    val securityDeposit: Int? = null,
    var advanceRent: Int? = null,
    var noticePeriod: Int? = null,
    val sellerContact: ContactModel? = null,
    val sellerAgentCommission: Double? = null,
    val buyerAgentCommission: Double? = null,
    val sellerAgentCommissionMoney: MoneyModel? = null,
    val buyerAgentCommissionMoney: MoneyModel? = null,
    val buyerAgentUser: UserModel? = null,
    val buyerContact: ContactModel? = null,
    var soldAt: Date? = null,
    var soldAtText: String? = null,
    var salePrice: MoneyModel? = null,
    val finalSellerAgentCommissionMoney: MoneyModel? = null,
    val finalBuyerAgentCommissionMoney: MoneyModel? = null,
    val published: Date? = null,
    val daysInMarket: Int? = null,
    val sellerAgentUser: UserModel? = null,
    val createdBy: UserModel? = null,
    val createdAt: Date = Date(),
    var modifiedAt: Date = Date(),
    var publishedAt: Date? = null,
    var publishedAtMoment: String? = null,
    var closedAt: Date? = null,
    var closedAtMoment: String? = null,

    // Legal information
    val landTitle: Boolean? = null,
    val technicalFile: Boolean? = null,
    val numberOfSigners: Int? = null,
    val mutationType: MutationType? = null,
    val transactionWithNotary: Boolean? = null,
    val morcelable: Boolean? = null,
    val subdivided: Boolean? = null,

    val title: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val publicUrl: String? = null,
    val totalImages: Int? = null,
    val totalOffers: Int? = null,
    val totalFiles: Int? = null,
    val totalLeads: Int? = null,
    val totalActiveMessages: Int? = null,
    val transactionParty: OfferParty? = null,
) {
    val publicRemarksHtml: String?
        get() = publicRemarks?.let { str -> HtmlUtils.toHtml(str) }

    val agentRemarksHtml: String?
        get() = agentRemarks?.let { str -> HtmlUtils.toHtml(str) }

    val descriptionHtml: String?
        get() = description?.let { str -> HtmlUtils.toHtml(str) }

    val listingTypeRental: Boolean
        get() = listingType == ListingType.RENTAL

    val listingTypeSale: Boolean
        get() = listingType == ListingType.SALE

    val propertyTypeResidential: Boolean
        get() = propertyType == PropertyType.APARTMENT ||
            propertyType == PropertyType.STUDIO ||
            propertyType == PropertyType.DUPLEX ||
            propertyType == PropertyType.VILLA ||
            propertyType == PropertyType.HOUSE

    val readOnly: Boolean
        get() = status != ListingStatus.DRAFT

    val statusDraft: Boolean
        get() = status == ListingStatus.DRAFT || status == ListingStatus.PUBLISHING

    val statusActive: Boolean
        get() = status == ListingStatus.ACTIVE || status == ListingStatus.ACTIVE_WITH_CONTINGENCIES

    val statusOnMarket: Boolean
        get() = status == ListingStatus.ACTIVE || status == ListingStatus.ACTIVE_WITH_CONTINGENCIES

    val statusOffMarket: Boolean
        get() = status == ListingStatus.SOLD ||
            status == ListingStatus.RENTED ||
            status == ListingStatus.EXPIRED ||
            status == ListingStatus.WITHDRAWN ||
            status == ListingStatus.CANCELLED

    fun commission(user: UserModel?): MoneyModel? {
        return if (statusSold) {
            if (user?.id == sellerAgentUser?.id) {
                finalSellerAgentCommissionMoney
            } else {
                finalBuyerAgentCommissionMoney
            }
        } else {
            if (user?.id == sellerAgentUser?.id) {
                sellerAgentCommissionMoney
            } else {
                buyerAgentCommissionMoney
            }
        }
    }

    val statusSold: Boolean
        get() = status == ListingStatus.SOLD ||
            status == ListingStatus.RENTED

    fun amenitiesByCategoryId(categoryId: Long): List<AmenityModel> {
        return amenities.filter { amenity -> amenity.categoryId == categoryId }
    }

    fun canSendMessage(user: UserModel?): Boolean {
        return (user != null) &&
            (sellerAgentUser != null) &&
            !sellerAgentUser.mobile.isNullOrEmpty() &&
            (sellerAgentUser.id != user.id)
    }

    fun canMakeOffer(user: UserModel?): Boolean {
        return (user != null) &&
            statusActive &&
            (sellerAgentUser != null)
    }

    fun canManage(user: UserModel?): Boolean {
        /*
         * - User can manage AND user is the seller agent
         * - user has full access
         */
        return ((user?.canManage("listing") == true) && (user.id == sellerAgentUser?.id)) ||
            (user?.hasFullAccess("listing") == true)
    }

    fun canAccess(user: UserModel?): Boolean {
        /*
         * - User can access AND user is the seller agent or listing is not draft
         * - user has full access
         */
        return ((user?.canAccess("listing") == true) && (user.id == sellerAgentUser?.id || !statusDraft)) ||
            (user?.hasFullAccess("listing") == true)
    }

    fun sellerAgent(user: UserModel?): Boolean {
        return sellerAgentUser?.id == user?.id
    }

    fun buyerAgent(user: UserModel?): Boolean {
        return buyerAgentUser?.id == user?.id
    }
}
