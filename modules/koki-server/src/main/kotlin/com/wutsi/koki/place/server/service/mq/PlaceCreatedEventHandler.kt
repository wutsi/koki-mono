package com.wutsi.koki.place.server.service.mq

import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.event.PlaceCreatedEvent
import com.wutsi.koki.place.server.domain.PlaceEntity
import com.wutsi.koki.place.server.service.ContentGeneratorAgentFactory
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.stereotype.Service

@Service
class PlaceCreatedEventHandler(
    private val placeService: PlaceService,
    private val contentGeneratorFactory: ContentGeneratorAgentFactory,
    private val locationService: LocationService,
) {
    fun handle(event: PlaceCreatedEvent): Boolean {
        val place = placeService.get(event.placeId)
        if (place.status != PlaceStatus.DRAFT) {
            return false
        }

        publishing(place)
        generateContent(place)
        published(place)

        return true
    }

    private fun generateContent(place: PlaceEntity) {
        val generator = contentGeneratorFactory.get(place.type)
        val neighbourhood = locationService.get(place.neighbourhoodId)
        val city = locationService.get(place.cityId)
        generator.generate(place, neighbourhood, city)
    }

    private fun publishing(place: PlaceEntity) {
        place.status = PlaceStatus.PUBLISHING
        placeService.save(place)
    }

    private fun published(place: PlaceEntity) {
        place.status = PlaceStatus.PUBLISHED
        placeService.save(place)
    }
}
