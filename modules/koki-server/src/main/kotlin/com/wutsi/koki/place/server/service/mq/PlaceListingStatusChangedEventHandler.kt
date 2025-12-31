package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.event.ListingStatusChangedEvent
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.LocationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PlaceListingStatusChangedEventHandler(
    private val locationService: LocationService,
    private val placeService: PlaceService,
    private val listingService: ListingService,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PlaceListingStatusChangedEventHandler::class.java)
    }

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
        val place = placeService.search(
            types = listOf(PlaceType.NEIGHBORHOOD),
            neighbourhoodIds = listOf(neighbourhoodId),
            limit = 1
        ).firstOrNull()
            ?: createNeighbourhood(neighbourhoodId)
        logger.add("place_id", place.id)

        if (!hasContent(place)) {
            LOGGER.info("Generating content for neighbourhood #${place.id} - ${place.name}")
            placeService.update(place.id ?: -1)
            logger.add("content_generated", true)
        }
        return true
    }

    private fun createNeighbourhood(neighbourhoodId: Long): PlaceEntity {
        val location = locationService.get(neighbourhoodId, LocationType.NEIGHBORHOOD)
        return placeService.create(
            CreatePlaceRequest(
                name = location.name,
                neighbourhoodId = neighbourhoodId,
                type = PlaceType.NEIGHBORHOOD,
                generateContent = false
            )
        )
    }

    private fun hasContent(place: PlaceEntity): Boolean {
        return !place.summary.isNullOrEmpty() &&
            !place.introduction.isNullOrEmpty() &&
            !place.description.isNullOrEmpty()
    }
}
