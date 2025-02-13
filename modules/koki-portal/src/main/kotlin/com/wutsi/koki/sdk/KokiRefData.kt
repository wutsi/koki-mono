package com.wutsi.koki.sdk

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.refdata.dto.SearchUnitResponse
import org.springframework.web.client.RestTemplate

class KokiRefData(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val UNIT_PATH_PREFIX = "/v1/units"
        private const val LOCATION_PATH_PREFIX = "/v1/locations"
    }

    fun units(): SearchUnitResponse {
        val url = urlBuilder.build(UNIT_PATH_PREFIX)
        return rest.getForEntity(url, SearchUnitResponse::class.java).body
    }

    fun locations(
        keyword: String?,
        ids: List<Long>,
        parentId: Long?,
        type: LocationType?,
        country: String?,
        limit: Int,
        offset: Int,
    ): SearchLocationResponse {
        val url = urlBuilder.build(
            LOCATION_PATH_PREFIX, mapOf(
                "q" to keyword,
                "id" to ids,
                "parent-id" to parentId,
                "type" to type,
                "country" to country,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchLocationResponse::class.java).body
    }
}
