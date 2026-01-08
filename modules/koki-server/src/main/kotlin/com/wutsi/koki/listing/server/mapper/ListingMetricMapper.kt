package com.wutsi.koki.listing.server.mapper

import com.wutsi.koki.listing.dto.NeighbourhoodMetricSummary
import com.wutsi.koki.listing.server.domain.NeighbourhoodMetricEntity
import org.springframework.stereotype.Service

@Service
class NeighbourhoodMetricMapper {
    fun toNeighbourhoodMetricSummary(entity: NeighbourhoodMetricEntity): NeighbourhoodMetricSummary {
        return NeighbourhoodMetricSummary(
            neighborhoodId = entity.neighborhoodId,
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
