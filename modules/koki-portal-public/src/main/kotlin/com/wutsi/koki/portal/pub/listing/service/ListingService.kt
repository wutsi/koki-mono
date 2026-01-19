package com.wutsi.koki.portal.pub.listing.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.model.ResultSetModel
import com.wutsi.koki.portal.pub.file.model.FileModel
import com.wutsi.koki.portal.pub.file.service.FileService
import com.wutsi.koki.portal.pub.listing.mapper.ListingMapper
import com.wutsi.koki.portal.pub.listing.model.ListingMetricModel
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.refdata.model.AmenityModel
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.refdata.service.AmenityService
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.user.model.UserModel
import com.wutsi.koki.portal.pub.user.service.UserService
import com.wutsi.koki.sdk.KokiListings
import org.springframework.stereotype.Service
import java.util.Collections.emptyMap

@Service
class ListingService(
    private val koki: KokiListings,
    private val mapper: ListingMapper,
    private val locationService: LocationService,
    private val userService: UserService,
    private val amenityService: AmenityService,
    private val fileService: FileService,
    private val agentService: AgentService,
) {
    fun get(id: Long, fullGraph: Boolean = true): ListingModel {
        val listing = koki.get(id).listing

        val locationIds =
            listOfNotNull(listing.address?.cityId, listing.address?.stateId, listing.address?.neighborhoodId)
                .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.search(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val userIds = listOfNotNull(listing.createdById, listing.sellerAgentUserId, listing.buyerAgentUserId)
            .distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.search(
                ids = userIds, limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val amenities = if (!fullGraph || listing.amenityIds.isEmpty()) {
            emptyMap<Long, AmenityModel>()
        } else {
            amenityService.amenities(
                ids = listing.amenityIds, limit = listing.amenityIds.size
            ).associateBy { amenity -> amenity.id }
        }

        val images = if (!fullGraph) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.search(
                type = FileType.IMAGE,
                ownerId = id,
                ownerType = ObjectType.LISTING,
                limit = 100,
            ).associateBy { image -> image.id }
        }

        val agentUserIds = listOfNotNull(listing.buyerAgentUserId, listing.sellerAgentUserId)
        val agents = if (agentUserIds.isEmpty() || !fullGraph) {
            emptyMap()
        } else {
            agentService.search(
                userIds = agentUserIds,
                limit = agentUserIds.size,
                fullGraph = false,
            ).associateBy { agent -> agent.id }
        }

        return mapper.toListingModel(
            entity = listing,
            locations = locations,
            users = users,
            amenities = amenities,
            images = images,
            agents = agents,
        )
    }

    fun getSimilar(
        id: Long,
        sameAgent: Boolean = false,
        sameNeighborhood: Boolean = false,
        sameCity: Boolean = false,
        limit: Int = 10,
    ): List<ListingModel> {
        val similar = koki.searchSimilar(
            id = id,
            statuses = listOf(
                ListingStatus.ACTIVE,
                ListingStatus.ACTIVE_WITH_CONTINGENCIES,
            ),
            sameAgent = sameAgent,
            sameNeighborhood = sameNeighborhood,
            sameCity = sameCity,
            limit = limit
        )
        val listingIds = similar.listings.map { listing -> listing.id }
        if (listingIds.isEmpty()) {
            return emptyList()
        }

        val listings = search(
            ids = listingIds,
            limit = listingIds.size,
        ).items.associateBy { listing -> listing.id }

        // Respect the order of the similar listings
        return similar.listings.mapNotNull { listing -> listings[listing.id] }
    }

    fun search(
        ids: List<Long> = emptyList(),
        listingNumber: Long? = null,
        locationIds: List<Long> = emptyList(),
        listingType: ListingType? = null,
        propertyTypes: List<PropertyType> = emptyList(),
        furnitureTypes: List<FurnitureType> = emptyList(),
        statuses: List<ListingStatus> = emptyList(),
        minBedrooms: Int? = null,
        maxBedrooms: Int? = null,
        minBathrooms: Int? = null,
        maxBathrooms: Int? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
        minLotArea: Int? = null,
        maxLotArea: Int? = null,
        minPropertyArea: Int? = null,
        maxPropertyArea: Int? = null,
        sellerAgentUserId: Long? = null,
        buyerAgentUserId: Long? = null,
        agentUserId: Long? = null,
        sortBy: ListingSort? = null,
        limit: Int = 20,
        offset: Int = 0,
        fullGraph: Boolean = true,
    ): ResultSetModel<ListingModel> {
        val response = koki.search(
            ids = ids,
            listingNumber = listingNumber,
            locationIds = locationIds,
            listingType = listingType,
            propertyTypes = propertyTypes,
            furnitureTypes = furnitureTypes,
            statuses = statuses,
            minBedrooms = minBedrooms,
            maxBedrooms = maxBedrooms,
            minBathrooms = minBathrooms,
            maxBathrooms = maxBathrooms,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minLotArea = minLotArea,
            maxLotArea = maxLotArea,
            minPropertyArea = minPropertyArea,
            maxPropertyArea = maxPropertyArea,
            sellerAgentUserId = sellerAgentUserId,
            buyerAgentUserId = buyerAgentUserId,
            agentUserId = agentUserId,
            sortBy = sortBy,
            limit = limit,
            offset = offset,
        )
        val listings = response.listings
        val locationIds = listings.flatMap { listing ->
            listOf(listing.address?.cityId, listing.address?.stateId, listing.address?.neighborhoodId)
        }
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.search(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val userIds = listings.flatMap { listing ->
            listOf(listing.sellerAgentUserId, listing.buyerAgentUserId)
        }
            .filterNotNull()
            .distinct()
        val users = if (!fullGraph || userIds.isEmpty()) {
            emptyMap<Long, UserModel>()
        } else {
            userService.search(
                ids = userIds, limit = userIds.size
            ).associateBy { user -> user.id }
        }

        val imageIds = listings.mapNotNull { listing -> listing.heroImageId }
        val images = if (!fullGraph || imageIds.isEmpty()) {
            emptyMap<Long, FileModel>()
        } else {
            fileService.search(
                ids = imageIds,
                limit = imageIds.size
            ).associateBy { image -> image.id }
        }

        return ResultSetModel(
            total = response.total,
            items = response.listings.map { listing ->
                mapper.toListingModel(
                    entity = listing,
                    locations = locations,
                    users = users,
                    images = images
                )
            }
        )
    }

    fun metrics(
        neighbourhoodId: Long? = null,
        sellerAgentUserIds: List<Long> = emptyList(),
        cityId: Long? = null,
        bedrooms: Int? = null,
        propertyCategory: PropertyCategory? = null,
        listingType: ListingType? = null,
        listingStatus: ListingStatus? = null,
        dimension: ListingMetricDimension? = null,
    ): List<ListingMetricModel> {
        val response = koki.metrics(
            neighbourhoodId = neighbourhoodId,
            sellerAgentUserIds = sellerAgentUserIds,
            cityId = cityId,
            bedrooms = bedrooms,
            propertyCategory = propertyCategory,
            listingType = listingType,
            listingStatus = listingStatus,
            dimension = dimension,
        )
        return response.metrics.map { metric -> mapper.toListingMetricModel(metric) }
    }
}
