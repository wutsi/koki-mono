package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class PlaceListingStatusChangedEventHandler(
    private val locationService: LocationService,
    private val placeService: PlaceService,
    private val listingService: ListingService,
    private val logger: KVLogger,
    private val publisher: Publisher,
) {
    fun handle(event: ListingStatusChangedEvent): Boolean {
        logger.add("event_status", event.status)
        logger.add("event_listing_id", event.listingId)
        logger.add("event_tenant_id", event.tenantId)

        if (event.status == ListingStatus.ACTIVE) {
            return createNeighbourhoodContent(event)
        } else {
            return false
        }
    }

    private fun createNeighbourhoodContent(event: ListingStatusChangedEvent): Boolean {
        val listing = listingService.get(event.listingId, event.tenantId)
        logger.add("neighbourhood_id", listing.neighbourhoodId)

        val neighbourhoodId = listing.neighbourhoodId ?: return false
        var place = placeService.search(
            types = listOf(PlaceType.NEIGHBORHOOD),
            neighbourhoodIds = listOf(neighbourhoodId),
            limit = 1
        ).firstOrNull()

        if (place == null) {
            val location = locationService.get(neighbourhoodId, LocationType.NEIGHBORHOOD)
            place = placeService.create(
                CreatePlaceRequest(
                    name = location.name,
                    neighbourhoodId = neighbourhoodId,
                    type = PlaceType.NEIGHBORHOOD,
                )
            )
            logger.add("place_id", place.id)

            publisher.publish(PlaceCreatedEvent(place.id ?: -1))
        } else if (!place.hasContent()) {
            val placeId = place.id ?: -1
            placeService.update(placeId)
            publisher.publish(PlaceUpdatedEvent(placeId)) // This will fire the content generation
        }
        return true
    }
}
