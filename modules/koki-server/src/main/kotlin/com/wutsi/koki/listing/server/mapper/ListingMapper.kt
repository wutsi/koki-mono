package com.wutsi.koki.listing.server.mapper

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.refdata.dto.Money
import org.springframework.stereotype.Service

@Service
class ListingMapper {
    fun toListing(entity: ListingEntity): Listing {
        return Listing(
            id = entity.id ?: -1,
            heroImageUrl = entity.heroImageUrl,
            status = entity.status,
            listingNumber = entity.listingNumber,
            listingType = entity.listingType?.takeIf { type -> type != ListingType.UNKNOWN },
            propertyType = entity.propertyType?.takeIf { type -> type != PropertyType.UNKNOWN },
            bedrooms = entity.bedrooms,
            bathrooms = entity.bathrooms,
            halfBathrooms = entity.halfBathrooms,
            floors = entity.floors,
            basementType = entity.basementType?.takeIf { type -> type != BasementType.UNKNOWN },
            level = entity.level,
            unit = entity.unit,
            parkingType = entity.parkingType?.takeIf { type -> type != ParkingType.UNKNOWN },
            parkings = entity.parkings,
            fenceType = entity.fenceType?.takeIf { type -> type != FenceType.UNKNOWN },
            lotArea = entity.lotArea,
            propertyArea = entity.propertyArea,
            year = entity.year,

            furnitureType = entity.furnitureType,
            amenityIds = entity.amenities.map { amenity -> amenity.id },

            address = toAddress(entity),

            geoLocation = toGeoLocation(entity),

            price = toMoney(entity.price, entity.currency),
            visitFees = toMoney(entity.visitFees, entity.currency),
            sellerAgentCommission = entity.sellerAgentCommission,
            buyerAgentCommission = entity.buyerAgentCommission,

            securityDeposit = toMoney(entity.securityDeposit, entity.currency),
            advanceRent = entity.advanceRent,
            leaseTerm = entity.leaseTerm,
            noticePeriod = entity.noticePeriod,

            sellerName = entity.sellerName?.ifEmpty { null },
            sellerPhone = entity.sellerPhone?.ifEmpty { null },
            sellerEmail = entity.sellerEmail?.ifEmpty { null },
            sellerIdNumber = entity.sellerIdNumber?.ifEmpty { null },
            sellerIdType = entity.sellerIdType,
            sellerIdCountry = entity.sellerIdCountry?.ifEmpty { null },

            agentRemarks = entity.agentRemarks?.ifEmpty { null },
            publicRemarks = entity.publicRemarks?.ifEmpty { null },

            description = entity.description?.ifEmpty { null },
            sellerAgentUserId = entity.sellerAgentUserId,
            createdById = entity.createdById,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            publishedAt = entity.publishedAt,
            closedAt = entity.closedAt,
        )
    }

    private fun toAddress(listing: ListingEntity): Address? {
        return if (
            listing.country == null &&
            listing.cityId == null &&
            listing.neighbourhoodId == null &&
            listing.street == null &&
            listing.postalCode == null
        ) {
            null
        } else {
            Address(
                country = listing.country?.ifEmpty { null },
                cityId = listing.cityId,
                neighborhoodId = listing.neighbourhoodId,
                street = listing.street?.ifEmpty { null },
                postalCode = listing.postalCode?.ifEmpty { null },
            )
        }
    }

    private fun toGeoLocation(listing: ListingEntity): GeoLocation? {
        return if (listing.latitude == null || listing.longitude == null) {
            null
        } else {
            GeoLocation(
                latitude = listing.latitude!!,
                longitude = listing.longitude!!
            )
        }
    }

    private fun toMoney(amount: Long?, currency: String?): Money? {
        return if (amount == null || amount == 0L) {
            null
        } else {
            Money(amount.toDouble(), currency)
        }
    }
}
