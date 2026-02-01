package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.event.PlaceUpdatedEvent
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ContentGeneratorAgentFactory
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class UpdatePlaceEventHandler(
    private val placeService: PlaceService,
    private val contentGeneratorFactory: ContentGeneratorAgentFactory,
    private val locationService: LocationService,
    private val logger: KVLogger,
) {
    fun handle(event: PlaceUpdatedEvent): Boolean {
        logger.add("event_place_id", event.placeId)

        val place = placeService.get(event.placeId)
        logger.add("place_sstatus", place.status)

        if (place.status == PlaceStatus.PUBLISHING) {
            return false
        } else {
            generateContent(place)
            return true
        }
    }

    private fun generateContent(place: PlaceEntity) {
        val generator = contentGeneratorFactory.get(place.type)
        val neighbourhood = locationService.get(place.neighbourhoodId)
        val city = locationService.get(place.cityId)
        generator.generate(place, neighbourhood, city)
    }
}
