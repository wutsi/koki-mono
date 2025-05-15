package com.wutsi.koki.portal.refdata.page

import com.wutsi.koki.portal.refdata.service.LocationService
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LocationSelectorController(private val service: LocationService) {
    @GetMapping("/locations/selector/search")
    fun search(
        @RequestParam(required = false, name = "q") keyword: String? = null,
        @RequestParam(required = false, name = "country") country: String? = null,
        @RequestParam(required = false, name = "parent-id") parentId: Long? = null,
        @RequestParam(required = false) type: LocationType = LocationType.CITY,
    ): List<Map<String, Any>> {
        // Find the location
        val locations = service.locations(
            keyword = keyword,
            country = country,
            parentId = parentId,
            type = type,
            limit = 20,
        )

        // Find the parents
        val parentIds = locations.map { location -> location.parentId }.filterNotNull().toSet()
        val parents = if (parentIds.isEmpty()) {
            emptyMap()
        } else {
            service.locations(
                ids = parentIds.toList(),
                limit = parentIds.size,
            ).associateBy { location -> location.id }
        }

        return locations.map { location ->
            mapOf(
                "id" to location.id,
                "name" to listOf(
                    location.name,
                    location.parentId?.let { id -> parents[id]?.name }
                ).filterNotNull().joinToString(", ")
            )
        }
    }
}
