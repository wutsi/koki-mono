package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.common.dto.ImportResponse
import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.refdata.dto.SearchLocationResponse
import com.wutsi.koki.refdata.server.io.GeonamesImporter
import com.wutsi.koki.refdata.server.mapper.LocationMapper
import com.wutsi.koki.refdata.server.service.LocationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/locations")
class LocationEndpoints(
    private val importer: GeonamesImporter,
    private val service: LocationService,
    private val mapper: LocationMapper,
) {
    @GetMapping("/import")
    fun import(@RequestParam country: String): ImportResponse {
        return importer.import(country)
    }

    @GetMapping
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "id") ids: List<Long> = emptyList(),
        @RequestParam(required = false, name = "parent-id") parentId: Long? = null,
        @RequestParam(required = false) type: LocationType? = null,
        @RequestParam(required = false) country: String? = null,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
    ): SearchLocationResponse {
        val locations = service.search(
            keyword = keyword,
            ids = ids,
            parentId = parentId,
            type = type,
            country = country,
            limit = limit,
            offset = offset,
        )
        return SearchLocationResponse(
            locations = locations.map { location -> mapper.toLocation(location) }
        )
    }
}
