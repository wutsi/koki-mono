package com.wutsi.koki.sdk

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.SearchRoomLocationMetricResponse
import org.springframework.web.client.RestTemplate

class KokiRoomLocationMetrics(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/room/metrics/locations"
    }

    fun metrics(
        ids: List<Long>,
        parentLocationId: Long?,
        locationType: LocationType?,
        country: String?,
        limit: Int,
        offset: Int,
    ): SearchRoomLocationMetricResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "id" to ids,
                "parent-location-id" to parentLocationId,
                "location-type" to locationType,
                "country" to country,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchRoomLocationMetricResponse::class.java).body
    }
}
