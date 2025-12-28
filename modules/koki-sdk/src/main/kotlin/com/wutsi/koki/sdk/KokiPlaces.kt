package com.wutsi.koki.sdk

import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.CreatePlaceResponse
import com.wutsi.koki.place.dto.GetPlaceResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SearchPlaceResponse
import org.springframework.web.client.RestTemplate

class KokiPlaces(
    private val urlBuilder: URLBuilder,
    rest: RestTemplate,
) : AbstractKokiModule(rest) {
    companion object {
        private const val PATH_PREFIX = "/v1/places"
    }

    fun create(request: CreatePlaceRequest): CreatePlaceResponse {
        val url = urlBuilder.build(PATH_PREFIX)
        return rest.postForEntity(url, request, CreatePlaceResponse::class.java).body!!
    }

    fun update(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.postForEntity(url, emptyMap<String, Any>(), Any::class.java)
    }

    fun get(id: Long): GetPlaceResponse {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        return rest.getForEntity(url, GetPlaceResponse::class.java).body!!
    }

    fun search(
        neighbourhoodIds: List<Long> = emptyList(),
        cityIds: List<Long> = emptyList(),
        types: List<PlaceType> = emptyList(),
        statuses: List<PlaceStatus> = emptyList(),
        keyword: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): SearchPlaceResponse {
        val url = urlBuilder.build(
            PATH_PREFIX,
            mapOf(
                "neighbourhood-id" to neighbourhoodIds,
                "city-id" to neighbourhoodIds,
                "type" to types,
                "status" to statuses,
                "q" to keyword,
                "limit" to limit,
                "offset" to offset,
            )
        )
        return rest.getForEntity(url, SearchPlaceResponse::class.java).body!!
    }

    fun delete(id: Long) {
        val url = urlBuilder.build("$PATH_PREFIX/$id")
        rest.delete(url)
    }
}
