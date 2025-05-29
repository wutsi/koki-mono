package com.wutsi.koki.room.web.location.page

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.web.common.model.PageModel
import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import com.wutsi.koki.room.web.location.model.MapMarkerModel
import com.wutsi.koki.room.web.refdata.model.LocationService
import com.wutsi.koki.room.web.room.service.RoomService
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.client.HttpClientErrorException

@Controller
@RequestMapping("/locations")
class LocationController(
    private val roomService: RoomService,
    private val service: LocationService,
) : AbstractPageController() {
    @GetMapping("/{id}/{title}")
    fun list(
        @PathVariable id: Long,
        @PathVariable title: String,
        model: Model
    ): String {
        val location = service.location(id)
        model.addAttribute("location", location)

        val city = if (location.type == LocationType.CITY) {
            location
        } else if (location.type == LocationType.NEIGHBORHOOD) {
            model.addAttribute("neighborhood", location)
            location.parentId?.let { parentId ->
                service.location(parentId)
            } ?: throw HttpClientErrorException(HttpStatusCode.valueOf(404))
        } else {
            null
        }
            ?: throw HttpClientErrorException(HttpStatusCode.valueOf(404))

        model.addAttribute("city", city)
        model.addAttribute("latitude", if (location.hasGeoLocation) location.latitude else city.latitude)
        model.addAttribute("longitude", if (location.hasGeoLocation) location.longitude else city.longitude)
        model.addAttribute(
            "zoom",
            if (location.hasGeoLocation && location.type == LocationType.NEIGHBORHOOD) 16 else 14
        )

        model.addAttribute(
            "page",
            PageModel(
                name = PageName.LOCATION,
                title = "${location.name} Rentals",
                description = "Find a house, apartment or room to rent in ${location.name}",
                url = "$baseUrl/locations/$id/$title",
            )
        )

        more(
            cityId = if (location.type == LocationType.CITY) id else null,
            neighborhoodId = if (location.type == LocationType.NEIGHBORHOOD) id else null,
            model = model,
        )
        return "locations/show"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false, name = "city-id") cityId: Long? = null,
        @RequestParam(required = false, name = "neighborhood-id") neighborhoodId: Long? = null,
        @RequestParam limit: Int = 20,
        @RequestParam offset: Int = 0,
        model: Model,
    ): String {
        val rooms = roomService.rooms(
            cityId = cityId,
            neighborhoodId = neighborhoodId,
            limit = limit,
            offset = offset,
        )
        model.addAttribute("rooms", rooms)

        if (rooms.size >= limit) {
            var url = "/locations/more?limit=$limit&offset=$offset"
            if (cityId != null) {
                url = "$url&city-id=$cityId"
            }
            if (neighborhoodId != null) {
                url = "$url&neighborhood-id=$neighborhoodId"
            }
            model.addAttribute("moreUrl", url)
        }
        return "locations/more"
    }

    @ResponseBody
    @GetMapping("/{cityId}/map")
    fun map(@PathVariable cityId: Long): List<MapMarkerModel> {
        return roomService.map(
            cityId = cityId,
            limit = 200,
        )
    }
}
