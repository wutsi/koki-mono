package com.wutsi.koki.place.server.endpoint

import com.wutsi.koki.place.dto.CreatePlaceRequest
import com.wutsi.koki.place.dto.CreatePlaceResponse
import com.wutsi.koki.place.dto.GetPlaceResponse
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.place.dto.SearchPlaceResponse
import com.wutsi.koki.place.server.service.PlaceService
import com.wutsi.koki.platform.logger.KVLogger
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/places")
class PlaceEndpoints(
    private val service: PlaceService,
    private val logger: KVLogger,
) {
    @PostMapping
    fun create(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @Valid @RequestBody request: CreatePlaceRequest,
    ): CreatePlaceResponse {
        logger.add("request_name", request.name)
        logger.add("request_type", request.type)
        logger.add("request_neighbourhood_id", request.neighbourhoodId)

        val place = service.create(request, tenantId)

        logger.add("response_place_id", place.id)
        return CreatePlaceResponse(placeId = place.id!!)
    }

    @GetMapping("/{id}")
    fun get(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ): GetPlaceResponse {
        val place = service.get(id, tenantId)
        return GetPlaceResponse(place = place)
    }

    @GetMapping
    fun search(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @RequestParam(required = false, name = "neighbourhood-id") neighbourhoodIds: List<Long>? = null,
        @RequestParam(required = false, name = "type") types: List<PlaceType>? = null,
        @RequestParam(required = false, name = "status") statuses: List<PlaceStatus>? = null,
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchPlaceResponse {
        val places = service.search(
            tenantId = tenantId,
            neighbourhoodIds = neighbourhoodIds,
            types = types,
            statuses = statuses,
            keyword = keyword,
            limit = limit,
            offset = offset,
        )
        return SearchPlaceResponse(places = places)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @RequestHeader(name = "X-Tenant-ID") tenantId: Long,
        @PathVariable id: Long,
    ) {
        service.delete(id, tenantId)
    }
}
