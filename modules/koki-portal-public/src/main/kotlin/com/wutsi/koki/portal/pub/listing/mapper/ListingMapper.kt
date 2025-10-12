package com.wutsi.koki.portal.pub.listing.mapper

import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.pub.common.mapper.MoneyMapper
import com.wutsi.koki.portal.pub.common.mapper.TenantAwareMapper
import com.wutsi.koki.portal.pub.common.model.MoneyModel
import com.wutsi.koki.portal.pub.common.service.Moment
import com.wutsi.koki.portal.pub.file.model.FileModel
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.refdata.model.AddressModel
import com.wutsi.koki.portal.pub.refdata.model.AmenityModel
import com.wutsi.koki.portal.pub.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.refdata.dto.Money
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class ListingMapper(
    private val moneyMapper: MoneyMapper,
    private val messages: MessageSource,
    private val moment: Moment,
) : TenantAwareMapper() {
    fun toListingModel(
        entity: Listing,
        locations: Map<Long, LocationModel>,
        users: Map<Long, UserModel>,
        amenities: Map<Long, AmenityModel>,
        images: Map<Long, FileModel>,
    ): ListingModel {
        val price = toPrice(entity.price, entity.listingType)
        val lang = LocaleContextHolder.getLocale().language
        val df = createDateFormat()
        val address = toAddress(entity.address, locations)

        return ListingModel(
            id = entity.id,
            status = entity.status,
            listingNumber = entity.listingNumber.toString(),
            listingType = entity.listingType,
            propertyType = entity.propertyType,
            bedrooms = entity.bedrooms,
            bathrooms = entity.bathrooms,
            halfBathrooms = entity.halfBathrooms,
            floors = entity.floors,
            basementType = entity.basementType,
            level = entity.level,
            unit = entity.unit,
            parkingType = entity.parkingType,
            parkings = entity.parkings,
            fenceType = entity.fenceType,
            lotArea = entity.lotArea,
            propertyArea = entity.propertyArea,
            year = entity.year,
            heroImageUrl = entity.heroImageId?.let { id -> images[id]?.contentUrl },
            distanceFromMainRoad = entity.distanceFromMainRoad,
            roadPavement = entity.roadPavement,
            availableAt = entity.availableAt,
            availableAtText = entity.availableAt?.let { date -> df.format(date) },

            furnitureType = entity.furnitureType,
            amenities = entity.amenityIds.mapNotNull { id -> amenities[id] },

            address = address,

            geoLocation = toGeoLocation(entity.geoLocation),

            price = price,
            visitFees = entity.visitFees?.let { money -> moneyMapper.toMoneyModel(money) },
            sellerAgentCommission = entity.sellerAgentCommission,
            buyerAgentCommission = entity.buyerAgentCommission,

            securityDeposit = entity.securityDeposit?.let { money -> moneyMapper.toMoneyModel(money) },
            advanceRent = entity.advanceRent,
            advanceRentMoney = toAdvanceRentMoney(entity),
            leaseTerm = entity.leaseTerm,
            noticePeriod = entity.noticePeriod,

            publicRemarks = entity.publicRemarks,

            buyerAgentUser = entity.buyerAgentUserId?.let { id -> users[id] },
            transactionDate = entity.transactionDate,
            transactionDateText = entity.transactionDate?.let { date -> df.format(date) },
            transactionPrice = toPrice(entity.transactionPrice, entity.listingType),

            title = if (lang == "fr") {
                entity.titleFr ?: entity.title
            } else {
                entity.title
            },
            summary = if (lang == "fr") {
                entity.summaryFr ?: entity.summary
            } else {
                entity.summary
            },
            description = if (lang == "fr") {
                entity.descriptionFr ?: entity.description
            } else {
                entity.description
            },
            publicUrl = if (lang == "fr") {
                entity.publicUrlFr ?: entity.publicUrl
            } else {
                entity.publicUrl
            },
            totalFiles = entity.totalFiles,
            totalImages = entity.totalImages,
            totalOffers = entity.totalOffers,

            sellerAgentUser = entity.sellerAgentUserId?.let { id -> users[id] },
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            publishedAt = entity.publishedAt,
            publishedAtMoment = entity.publishedAt?.let { date -> moment.format(date) },
            closedAt = entity.closedAt,
            closedAtMoment = entity.closedAt?.let { date -> moment.format(date) },
            images = images.values.toList(),
        )
    }

//    fun toListingModel(
//        entity: ListingSummary,
//        locations: Map<Long, LocationModel>,
//        users: Map<Long, UserModel>,
//        images: Map<Long, FileModel>
//    ): ListingModel {
//        val price = toPrice(entity.price, entity.listingType)
//        val lang = LocaleContextHolder.getLocale().language
//        val df = createDateFormat()
//        val address = toAddress(entity.address, locations)
//
//        return ListingModel(
//            id = entity.id,
//            status = entity.status,
//            listingNumber = entity.listingNumber.toString(),
//            listingType = entity.listingType,
//            propertyType = entity.propertyType,
//            bedrooms = entity.bedrooms,
//            bathrooms = entity.bathrooms,
//            halfBathrooms = entity.halfBathrooms,
//            lotArea = entity.lotArea,
//            propertyArea = entity.propertyArea,
//            heroImageUrl = entity.heroImageId?.let { id -> images[id]?.contentUrl },
//            furnitureType = entity.furnitureType,
//            address = address,
//            price = price,
//            buyerAgentUser = entity.buyerAgentUserId?.let { id -> users[id] },
//            sellerAgentCommission = entity.sellerAgentCommission,
//            buyerAgentCommission = entity.buyerAgentCommission,
//            sellerAgentUser = entity.sellerAgentUserId?.let { id -> users[id] },
//            transactionDate = entity.transactionDate,
//            transactionDateText = entity.transactionDate?.let { date -> df.format(date) },
//            transactionPrice = toPrice(entity.transactionPrice, entity.listingType),
//            title = if (lang == "fr") {
//                entity.titleFr ?: entity.title
//            } else {
//                entity.title
//            },
//            summary = if (lang == "fr") {
//                entity.summaryFr ?: entity.summary
//            } else {
//                entity.summary
//            },
//            publicUrl = if (lang == "fr") {
//                entity.publicUrlFr ?: entity.publicUrl
//            } else {
//                entity.publicUrl
//            }
//        )
//    }

    private fun toAddress(address: Address?, locations: Map<Long, LocationModel>): AddressModel? {
        address ?: return null
        return AddressModel(
            country = address.country,
            city = address.cityId?.let { id -> locations[id] },
            neighbourhood = address.neighborhoodId?.let { id -> locations[id] },
            state = address.stateId?.let { id -> locations[id] },
            street = address.street,
            postalCode = address.postalCode,
            countryName = address.country?.let { country ->
                Locale(LocaleContextHolder.getLocale().language, country).getDisplayCountry()
            },
        )
    }

    private fun toGeoLocation(geoLocation: GeoLocation?): GeoLocationModel? {
        return geoLocation?.let { geo ->
            GeoLocationModel(
                latitude = geo.latitude,
                longitude = geo.longitude
            )
        }
    }

    private fun toPrice(price: Money?, listingType: ListingType?): MoneyModel? {
        price ?: return null

        val price = moneyMapper.toMoneyModel(price)
        if (listingType == ListingType.RENTAL) {
            val locale = LocaleContextHolder.getLocale()
            return price.copy(
                displayText = price.text +
                    " " +
                    messages.getMessage("page.listing.rental-price-suffix", emptyArray(), locale)
            )
        } else {
            return price
        }
    }

    private fun toAdvanceRentMoney(entity: Listing): MoneyModel? {
        return entity.price?.let { price ->
            entity.advanceRent?.let { advanceRent ->
                moneyMapper.toMoneyModel(
                    Money(
                        amount = (price.amount.toDouble() * advanceRent).toDouble(),
                        currency = price.currency
                    )
                )
            }
        }
    }
}
