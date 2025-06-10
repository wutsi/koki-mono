package com.wutsi.koki.room.server.endpoint

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.SearchRoomLocationMetricResponse
import com.wutsi.koki.room.server.mapper.RoomLocationStatsMapper
import com.wutsi.koki.room.server.service.RoomLocationMetricService
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/rooms/metrics/locations")
class RoomLocationMetricEndpoints(
    private val service: RoomLocationMetricService,
    private val mapper: RoomLocationStatsMapper,
) {
    @Async
    @GetMapping("/compile")
    fun compile() {
        service.compile()
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(name = "location-type", required = false) locationType: LocationType? = null,
        @RequestParam(name = "parent-location-id", required = false) parentLocationId: Long? = null,
        @RequestParam(required = false) country: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0
    ): SearchRoomLocationMetricResponse {
        val entities = service.search(
            tenantId = tenantId,
            ids = ids,
            parentLocationId = parentLocationId,
            locationType = locationType,
            country = country,
            limit = limit,
            offset = offset,
        )
        return SearchRoomLocationMetricResponse(
            metrics = entities.map { stat -> mapper.toRoomLocationStats(stat) }
        )
    }
}
