package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class PlaceContentGeneratorWorker(
    private val locationService: LocationService,
    private val placeService: PlaceService,
    private val placeCreatedEventHandler: PlaceCreatedEventHandler,
    private val placeUpdatePlaceEventHandler: UpdatePlaceEventHandler,
) {
    fun generate(locationId: Long, locationType: LocationType): Boolean {
        val placeType = when (locationType) {
            LocationType.NEIGHBORHOOD -> PlaceType.NEIGHBORHOOD
            LocationType.CITY -> PlaceType.CITY
            else -> null
        } ?: return false

        val neighbourhoodId = if (placeType == PlaceType.NEIGHBORHOOD) locationId else null
        val cityId = if (placeType == PlaceType.CITY) locationId else null
        var place = placeService.search(
            types = listOf(placeType),
            neighbourhoodIds = neighbourhoodId?.let { listOf(neighbourhoodId) },
            cityIds = cityId?.let { listOf(cityId) },
            limit = 1
        ).firstOrNull()

        if (place == null) {
            val location = locationService.get(locationId, locationType)
            place = placeService.create(
                CreatePlaceRequest(
                    name = location.name,
                    neighbourhoodId = neighbourhoodId,
                    cityId = (if (locationType == LocationType.NEIGHBORHOOD) location.parentId else cityId) ?: -1,
                    type = placeType,
                )
            )
            placeCreatedEventHandler.handle(PlaceCreatedEvent(place.id!!))
        } else if (place.status != PlaceStatus.PUBLISHING && place.hasNoContent()) {
            placeUpdatePlaceEventHandler.handle(PlaceUpdatedEvent(place.id!!))
        }
        return true
    }
}
