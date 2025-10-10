package com.wutsi.koki.portal.pub.listing.service

import com.wutsi.koki.listing.dto.FurnitureType
import com.wutsi.koki.listing.dto.Listing
import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.pub.common.model.ResultSetModel
import com.wutsi.koki.portal.pub.file.model.FileModel
import com.wutsi.koki.portal.pub.file.service.FileService
import com.wutsi.koki.portal.pub.listing.mapper.ListingMapper
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
) {
    fun get(id: Long, fullGraph: Boolean = true): ListingModel {
        val listing = koki.get(id).listing

        val locationIds = listOf(listing.address?.cityId, listing.address?.stateId, listing.address?.neighborhoodId)
            .filterNotNull()
            .distinct()
        val locations = if (!fullGraph || locationIds.isEmpty()) {
            emptyMap<Long, LocationModel>()
        } else {
            locationService.search(
                ids = locationIds, limit = locationIds.size
            ).associateBy { location -> location.id }
        }

        val userIds = listOf(listing.createdById, listing.sellerAgentUserId, listing.buyerAgentUserId)
            .filterNotNull()
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

        val images = if (!fullGraph || listing.heroImageId == null) {
            emptyMap<Long, FileModel>()
        } else {
            mapOf(
                listing.heroImageId!! to findHeroImage(listing)
            )
        }

        return mapper.toListingModel(
            entity = listing,
            locations = locations,
            users = users,
            amenities = amenities,
            images = images,
        )
    }

    fun search(
        ids: List<Long> = emptyList(),
        listingNumber: Long? = null,
        locationIds: List<Long> = emptyList(),
        listingType: ListingType? = null,
        propertyTypes: List<PropertyType> = emptyList(),
        furnitureTypes: List<FurnitureType> = emptyList(),
        statuses: List<ListingStatus> = emptyList(),
        bedrooms: String? = null,
        bathrooms: String? = null,
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
            bedrooms = bedrooms,
            bathrooms = bathrooms,
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

        return ResultSetModel<ListingModel>(
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

    private fun findHeroImage(listing: Listing): FileModel? {
        return try {
            listing.heroImageId?.let { id -> fileService.get(id) }
        } catch (ex: Exception) {
            null
        }
    }
}
