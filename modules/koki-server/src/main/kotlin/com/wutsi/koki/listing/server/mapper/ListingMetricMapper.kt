package com.wutsi.koki.listing.server.mapper

import com.wutsi.koki.listing.dto.ListingMetricSummary
import com.wutsi.koki.listing.server.domain.ListingMetricEntity
import org.springframework.stereotype.Service

@Service
class ListingMetricMapper {
    fun toListingLocationMetricSummary(entity: ListingMetricEntity): ListingMetricSummary {
        return ListingMetricSummary(
            neighborhoodId = entity.neighborhoodId?.takeIf { id -> id > 0 },
            cityId = entity.cityId?.takeIf { id -> id > 0 },
            sellerAgentUserId = entity.sellerAgentUserId?.takeIf { id -> id > 0 },
            bedrooms = if (entity.bedrooms != null && entity.bedrooms < 0) null else entity.bedrooms,
            propertyCategory = entity.propertyCategory,
            listingStatus = entity.listingStatus,
            listingType = entity.listingType,
            total = entity.totalListings.toLong(),
            minPrice = entity.minPrice,
            maxPrice = entity.maxPrice,
            averagePrice = entity.averagePrice,
            averageLotArea = entity.averageLotArea,
            pricePerSquareMeter = entity.pricePerSquareMeter,
            totalPrice = entity.totalPrice,
            currency = entity.currency,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
        )
    }
}
