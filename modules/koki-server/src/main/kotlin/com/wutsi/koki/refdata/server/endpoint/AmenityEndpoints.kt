package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.SearchAmenityResponse
import com.wutsi.koki.refdata.server.io.AmenityImporter
import com.wutsi.koki.refdata.server.mapper.AmenityMapper
import com.wutsi.koki.refdata.server.service.AmenityService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/amenities")
class AmenityEndpoints(
    private val importer: AmenityImporter,
    private val service: AmenityService,
    private val mapper: AmenityMapper,
) {
    @GetMapping("/import")
    fun import(): ImportResponse {
        return importer.import()
    }

    @GetMapping
    fun search(
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "category-id") categoryId: Long? = null,
        @RequestParam(required = false) active: Boolean? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchAmenityResponse {
        val amenities = service.search(
            ids = ids,
            categoryId = categoryId,
            active = active,
            limit = limit,
            offset = offset,
        )
        return SearchAmenityResponse(
            amenities = amenities.map { amenity -> mapper.toAmenity(amenity) }
        )
    }
}
