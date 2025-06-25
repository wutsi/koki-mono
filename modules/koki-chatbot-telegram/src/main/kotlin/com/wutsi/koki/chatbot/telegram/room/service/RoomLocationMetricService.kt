package com.wutsi.koki.room.web.room.service

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.web.refdata.service.LocationService
import com.wutsi.koki.room.web.room.mapper.RoomLocationMetricMapper
import com.wutsi.koki.room.web.room.model.RoomLocationMetricModel
import com.wutsi.koki.sdk.KokiRoomLocationMetrics
import org.springframework.stereotype.Service

@Service
class RoomLocationMetricService(
    private val koki: KokiRoomLocationMetrics,
    private val mapper: RoomLocationMetricMapper,
    private val locationService: LocationService,
) {
    fun metrics(
        ids: List<Long> = emptyList(),
        parentLocationId: Long? = null,
        locationType: LocationType? = null,
        country: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<RoomLocationMetricModel> {
        val entities = koki.metrics(
            ids = ids,
            parentLocationId = parentLocationId,
            locationType = locationType,
            country = country,
            limit = limit,
            offset = offset,
        ).metrics

        val locationIds = entities.map { entity -> entity.locationId }
        val locations = locationService.locations(
            ids = locationIds,
            limit = locationIds.size,
        ).associateBy { location -> location.id }

        return entities.mapNotNull { entity ->
            locations[entity.locationId]?.let { location -> mapper.toRoomLocationMetricModel(entity, location) }
        }
    }
}
