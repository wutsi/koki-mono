package com.wutsi.koki.listing.server.mapper

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingSummary
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.listing.dto.RoadPavement
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.platform.util.StringUtils
import com.wutsi.koki.refdata.dto.Address
import com.wutsi.koki.refdata.dto.GeoLocation
import com.wutsi.koki.refdata.dto.Money
import org.springframework.stereotype.Service

@Service
class ListingMapper {
    fun toListing(entity: ListingEntity): Listing {
        return Listing(
            id = entity.id ?: -1,
            heroImageId = entity.heroImageId,
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
            distanceFromMainRoad = entity.distanceFromMainRoad,
            roadPavement = entity.roadPavement?.takeIf { type -> type != RoadPavement.UNKNOWN },
            availableAt = entity.availableAt,

            furnitureType = entity.furnitureType?.takeIf { type -> type != FurnitureType.UNKNOWN },
            amenityIds = entity.amenities.map { amenity -> amenity.id },

            address = toAddress(entity),

            geoLocation = toGeoLocation(entity),

            price = toMoney(entity.price, entity.currency),
            visitFees = toMoney(entity.visitFees, entity.currency),
            sellerAgentCommission = entity.sellerAgentCommission,
            buyerAgentCommission = entity.buyerAgentCommission,
            sellerAgentCommissionMoney = toMoney(entity.sellerAgentCommissionAmount, entity.currency),
            buyerAgentCommissionMoney = toMoney(entity.buyerAgentCommissionAmount, entity.currency),

            securityDeposit = toMoney(entity.securityDeposit, entity.currency),
            advanceRent = entity.advanceRent,
            leaseTerm = entity.leaseTerm,
            noticePeriod = entity.noticePeriod,

            sellerContactId = entity.sellerContactId,

            agentRemarks = entity.agentRemarks?.ifEmpty { null },
            publicRemarks = entity.publicRemarks?.ifEmpty { null },

            soldAt = entity.soldAt,
            salePrice = toMoney(entity.salePrice, entity.currency),
            buyerAgentUserId = entity.buyerAgentUserId,
            buyerContactId = entity.buyerContactId,
            closedOfferId = entity.closedOfferId,

            finalSellerAgentCommissionMoney = toMoney(entity.finalSellerAgentCommissionAmount, entity.currency),
            finalBuyerAgentCommissionMoney = toMoney(entity.finalBuyerAgentCommissionAmount, entity.currency),

            title = entity.title?.ifEmpty { null },
            summary = entity.summary?.ifEmpty { null },
            description = entity.description?.ifEmpty { null },
            titleFr = entity.titleFr?.ifEmpty { null },
            summaryFr = entity.summaryFr?.ifEmpty { null },
            descriptionFr = entity.descriptionFr?.ifEmpty { null },

            totalImages = entity.totalImages,
            totalFiles = entity.totalFiles,
            totalOffers = entity.totalOffers,
            totalLeads = entity.totalLeads,

            sellerAgentUserId = entity.sellerAgentUserId,
            createdById = entity.createdById,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            publishedAt = entity.publishedAt,
            closedAt = entity.closedAt,

            publicUrl = toPublicUrl(entity.id, entity.title, entity.status),
            publicUrlFr = toPublicUrl(entity.id, entity.titleFr, entity.status),
        )
    }

    fun toListingSummary(entity: ListingEntity): ListingSummary {
        return ListingSummary(
            id = entity.id ?: -1,
            heroImageId = entity.heroImageId,
            status = entity.status,
            listingNumber = entity.listingNumber,
            listingType = entity.listingType?.takeIf { type -> type != ListingType.UNKNOWN },
            propertyType = entity.propertyType?.takeIf { type -> type != PropertyType.UNKNOWN },
            bedrooms = entity.bedrooms,
            bathrooms = entity.bathrooms,
            halfBathrooms = entity.halfBathrooms,
            lotArea = entity.lotArea,
            propertyArea = entity.propertyArea,

            furnitureType = entity.furnitureType,

            address = toAddress(entity),

            geoLocation = toGeoLocation(entity),

            price = toMoney(entity.price, entity.currency),
            sellerAgentCommission = entity.sellerAgentCommission,
            buyerAgentCommission = entity.buyerAgentCommission,
            sellerAgentCommissionMoney = toMoney(entity.sellerAgentCommissionAmount, entity.currency),
            buyerAgentCommissionMoney = toMoney(entity.buyerAgentCommissionAmount, entity.currency),

            transactionDate = entity.soldAt,
            transactionPrice = toMoney(entity.salePrice, entity.currency),
            buyerAgentUserId = entity.buyerAgentUserId,

            sellerAgentUserId = entity.sellerAgentUserId,
            finalSellerAgentCommissionMoney = toMoney(entity.finalSellerAgentCommissionAmount, entity.currency),
            finalBuyerAgentCommissionMoney = toMoney(entity.finalBuyerAgentCommissionAmount, entity.currency),

            title = entity.title?.ifEmpty { null },
            summary = entity.summary?.ifEmpty { null },
            titleFr = entity.titleFr?.ifEmpty { null },
            summaryFr = entity.summaryFr?.ifEmpty { null },

            publicUrl = toPublicUrl(entity.id, entity.title, entity.status),
            publicUrlFr = toPublicUrl(entity.id, entity.titleFr, entity.status),
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

    private fun toPublicUrl(id: Long?, title: String?, status: ListingStatus): String? {
        return when (status) {
            ListingStatus.ACTIVE,
            ListingStatus.ACTIVE_WITH_CONTINGENCIES,
            ListingStatus.SOLD,
            ListingStatus.RENTED,
            ListingStatus.PENDING -> {
                val prefix = "/listings/$id"
                title?.let { StringUtils.toSlug(prefix, title) } ?: prefix
            }

            else -> null
        }
    }
}
