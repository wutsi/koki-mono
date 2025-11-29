package com.wutsi.koki.portal.pub.listing.service

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
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

        return mapper.toListingModel(
            entity = listing,
            locations = locations,
            users = users,
            amenities = amenities,
            images = images,
        )
    }
}
