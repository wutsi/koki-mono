package com.wutsi.koki.portal.listing.mapper

import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.portal.common.mapper.MoneyMapper
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.AmenityModel
import com.wutsi.koki.portal.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Service

@Service
class ListingMapper(private val moneyMapper: MoneyMapper) {
    fun toListingModel(
        entity: Listing,
        locations: Map<Long, LocationModel>,
        users: Map<Long, UserModel>,
        amenities: Map<Long, AmenityModel>
    ): ListingModel {
        return ListingModel(
            id = entity.id,
            status = entity.status,
            listingNumber = entity.listingNumber,
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

            furnitureType = entity.furnitureType,
            amenities = entity.amenityIds.mapNotNull { id -> amenities[id] },

            address = toAddress(entity, locations),

            geoLocation = toGeoLocation(entity),

            price = entity.price?.let { money -> moneyMapper.toMoneyModel(money) },
            visitFees = entity.visitFees?.let { money -> moneyMapper.toMoneyModel(money) },
            sellerAgentCommission = entity.sellerAgentCommission,
            buyerAgentCommission = entity.buyerAgentCommission,

            securityDeposit = entity.securityDeposit?.let { money -> moneyMapper.toMoneyModel(money) },
            advanceRent = entity.advanceRent,
            leaseTerm = entity.leaseTerm,
            noticePeriod = entity.noticePeriod,

            sellerName = entity.sellerName,
            sellerPhone = entity.sellerPhone,
            sellerEmail = entity.sellerEmail,
            sellerIdNumber = entity.sellerIdNumber,
            sellerIdType = entity.sellerIdType,
            sellerIdCountry = entity.sellerIdCountry,

            agentRemarks = entity.agentRemarks,
            publicRemarks = entity.publicRemarks,

            description = entity.description,
            sellerAgentUser = entity.sellerAgentUserId?.let { id -> users[id] },
            createdBy = entity.createdById?.let { id -> users[id] },
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            publishedAt = entity.publishedAt,
            closedAt = entity.closedAt,
        )
    }

    private fun toAddress(listing: Listing, locations: Map<Long, LocationModel>): AddressModel? {
        val address = listing.address ?: return null
        return AddressModel(
            country = address.country,
            city = address.cityId?.let { id -> locations[id] },
            neighbourhood = address.neighborhoodId?.let { id -> locations[id] },
            state = address.stateId?.let { id -> locations[id] },
            street = address.street,
            postalCode = address.postalCode,
        )
    }

    private fun toGeoLocation(listing: Listing): GeoLocationModel? {
        return listing.geoLocation?.let { geo ->
            GeoLocationModel(
                latitude = geo.latitude,
                longitude = geo.longitude
            )
        }
    }
}
