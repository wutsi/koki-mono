package com.wutsi.koki.room.web.room.page

import com.wutsi.koki.refdata.dto.LocationType
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.LeaseType
import com.wutsi.koki.room.dto.RoomType
import com.wutsi.koki.room.web.common.page.AbstractPageController
import com.wutsi.koki.room.web.common.page.PageName
import com.wutsi.koki.room.web.geoip.service.CurrentGeoIPHolder
import com.wutsi.koki.room.web.refdata.model.LocationModel
import com.wutsi.koki.room.web.refdata.service.LocationService
import com.wutsi.koki.room.web.room.form.SearchForm
import com.wutsi.koki.room.web.room.model.MapMarkerModel
import com.wutsi.koki.room.web.room.model.RoomModel
import com.wutsi.koki.room.web.room.service.RoomLocationMetricService
import com.wutsi.koki.room.web.room.service.RoomService
import org.springframework.context.i18n.LocaleContextHolder
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
@RequestMapping("/l")
class LocationController(
    private val roomService: RoomService,
    private val metricService: RoomLocationMetricService,
    private val service: LocationService,
    private val geoIp: CurrentGeoIPHolder
) : AbstractPageController() {
    @GetMapping
    fun show(): String {
        var city = getGeoLocalizedCity()
            ?: getMostPopularCities(1).firstOrNull()
            ?: throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Unable to resolve geoIp data")
        return "redirect:${city.url}"
    }

    @GetMapping("/{id}/{title}")
    fun list(
        @PathVariable id: Long,
        @PathVariable title: String,
        @RequestParam(name = "lease-type", required = false) leaseType: String? = null,
        @RequestParam(name = "room-type", required = false) roomType: String? = null,
        @RequestParam(name = "furnished-type", required = false) furnishedType: String? = null,
        @RequestParam(required = false) bedrooms: Int? = null,
        model: Model,
    ): String {
        val location = service.location(id)
        model.addAttribute("location", location)

        val city = if (location.type == LocationType.CITY) {
            location
        } else if (location.type == LocationType.NEIGHBORHOOD) {
            model.addAttribute("neighborhood", location)
            service.location(location.parentId ?: -1)
        } else {
            throw HttpClientErrorException(HttpStatusCode.valueOf(404))
        }

        model.addAttribute("city", city)
        model.addAttribute("latitude", if (location.hasGeoLocation) location.latitude else city.latitude)
        model.addAttribute("longitude", if (location.hasGeoLocation) location.longitude else city.longitude)
        model.addAttribute(
            "zoom",
            if (location.hasGeoLocation && location.type == LocationType.NEIGHBORHOOD) 16 else 14
        )

        val tenant = tenantHolder.get()!!
        val locale = LocaleContextHolder.getLocale()
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LOCATION,
                title = getMessage("page.location.html.title", arrayOf(location.name)),
                description = getMessage("page.location.html.description", arrayOf(tenant.name, location.name), locale),
                url = "$baseUrl${location.url}",
            )
        )

        loadSearchForm(leaseType, roomType, furnishedType, bedrooms, model)

        more(
            cityId = if (location.type == LocationType.CITY) id else null,
            neighborhoodId = if (location.type == LocationType.NEIGHBORHOOD) id else null,
            leaseType = leaseType,
            roomType = roomType,
            furnishedType = furnishedType,
            bedrooms = bedrooms,
            model = model,
        )

        val rooms = model.getAttribute("rooms") as List<RoomModel>
        if (rooms.isEmpty()) {
            val popularLocations = getMostPopularCities(10)
            model.addAttribute("popularLocations", popularLocations)
        }
        return "rooms/location"
    }

    private fun loadSearchForm(
        leaseType: String?,
        roomType: String?,
        furnishedType: String?,
        bedrooms: Int?,
        model: Model,
    ) {
        if (
            !leaseType.isNullOrEmpty() ||
            !roomType.isNullOrEmpty() ||
            !furnishedType.isNullOrEmpty() ||
            (bedrooms != null && bedrooms > 0)
        ) {
            model.addAttribute(
                "searchForm",
                SearchForm(
                    roomType = roomType?.ifEmpty { null },
                    leaseType = leaseType?.ifEmpty { null },
                    furnishedType = furnishedType?.ifEmpty { null },
                    bedrooms = if (bedrooms == null || bedrooms < 0) null else bedrooms,
                )
            )
        }
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false, name = "city-id") cityId: Long? = null,
        @RequestParam(required = false, name = "neighborhood-id") neighborhoodId: Long? = null,
        @RequestParam(name = "lease-type", required = false) leaseType: String? = null,
        @RequestParam(name = "room-type", required = false) roomType: String? = null,
        @RequestParam(name = "furnished-type", required = false) furnishedType: String? = null,
        @RequestParam(required = false) bedrooms: Int? = null,
        @RequestParam limit: Int = 20,
        @RequestParam offset: Int = 0,
        model: Model,
    ): String {
        val rooms = roomService.rooms(
            cityId = cityId,
            neighborhoodId = neighborhoodId,
            limit = limit,
            offset = offset,

            minBedrooms = if (bedrooms == -1) null else bedrooms,
            maxBedrooms = if (bedrooms == -1 || bedrooms == 5) null else bedrooms,
            leaseType = leaseType?.let { value ->
                try {
                    LeaseType.valueOf(value)
                } catch (ex: Exception) {
                    null
                }
            },
            furnishedType = furnishedType?.let { value ->
                try {
                    FurnishedType.valueOf(value)
                } catch (ex: Exception) {
                    null
                }
            },
            types = roomType?.let { value ->
                try {
                    listOf(RoomType.valueOf(value))
                } catch (ex: Exception) {
                    emptyList<RoomType>()
                }
            } ?: emptyList<RoomType>(),
        )
        model.addAttribute("rooms", rooms)
        model.addAttribute("roomIds", rooms.map { room -> room.id }.joinToString("|"))
        model.addAttribute("offset", offset)

        if (rooms.size >= limit) {
            var url = "/l/more?limit=$limit&offset=$offset"
            if (cityId != null) {
                url = "$url&city-id=$cityId"
            }
            if (neighborhoodId != null) {
                url = "$url&neighborhood-id=$neighborhoodId"
            }
            if (leaseType != null) {
                url = "$url&lease-type=$leaseType"
            }
            if (roomType != null) {
                url = "$url&room-type=$roomType"
            }
            if (furnishedType != null) {
                url = "$url&furnished-type=$furnishedType"
            }
            if (bedrooms != null) {
                url = "$url&bedrooms=$bedrooms"
            }
            model.addAttribute("moreUrl", url)
        }
        return "rooms/more"
    }

    @ResponseBody
    @GetMapping("/{cityId}/map")
    fun map(@PathVariable cityId: Long): List<MapMarkerModel> {
        return roomService.map(
            cityId = cityId,
            limit = 200,
        )
    }

    @ResponseBody
    @GetMapping("/map/rooms/{roomId}")
    fun room(@PathVariable roomId: Long): RoomModel {
        return roomService.room(roomId, fullGraph = true)
    }

    private fun getGeoLocalizedCity(): LocationModel? {
        val geo = geoIp.get()
            ?: return null

        return service.locations(
            country = geo.countryCode,
            keyword = geo.city,
            limit = 1
        ).firstOrNull()
    }

    private fun getMostPopularCities(limit: Int): List<LocationModel> {
        val metrics = metricService.metrics(
            locationType = LocationType.CITY,
            limit = limit,
        )
        return metrics.map { metric -> metric.location }
    }
}
