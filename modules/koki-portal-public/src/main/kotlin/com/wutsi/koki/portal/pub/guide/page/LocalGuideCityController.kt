package com.wutsi.koki.portal.pub.guide.page

import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.place.dto.PlaceSort
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.place.service.PlaceService
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.whatsapp.service.WhatsappService
import com.wutsi.koki.refdata.dto.LocationType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/local-guides/cities")
class LocalGuideCityController(
    locationService: LocationService,
    listingService: ListingService,
    agentService: AgentService,
    placeService: PlaceService,
    whatsapp: WhatsappService
) : AbstractLocalGuideController(locationService, listingService, agentService, placeService, whatsapp) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LocalGuideCityController::class.java)
    }

    @GetMapping("/{id}/{slug}")
    fun show(@PathVariable id: Long, @PathVariable slug: String, model: Model): String {
        return show(id, model)
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        // City
        val city = locationService.get(id, LocationType.CITY)
        model.addAttribute("city", city)

        // Places
        val place = loadPlace(city, model)
        if (place != null) {
            loadNeighborhoods(city, model)
            loadCities(city.id, model)
        }

        // Listings
        loadActiveListings("rental", city.id, ListingType.RENTAL, model)
        loadActiveListings("sale", city.id, ListingType.SALE, model)

        // Agents
        loadAgents(city, model)

        // Price Trends
        loadPriceTrendMetrics(city, model)

        val tenant = tenantHolder.get()
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LOCAL_GUIDE_CITY,
                title = getMessage(
                    "page.local-guide.city.show.meta.title",
                    arrayOf(city.name, tenant.name),
                ),
                url = city.publicUrl,
                description = place?.summary,
                image = place?.heroImageUrl,
            ),
        )
        return "local-guides/city"
    }

    private fun loadNeighborhoods(city: LocationModel, model: Model): List<PlaceModel> {
        try {
            val places = placeService.search(
                cityIds = listOf(city.id),
                types = listOf(PlaceType.NEIGHBORHOOD),
                statuses = listOf(PlaceStatus.PUBLISHED),
                sort = PlaceSort.RATING_HIGH_LOW,
                limit = 12,
            )
            val locations = findLocations(places, LocationType.NEIGHBORHOOD)
            if (locations.isNotEmpty()) {
                model.addAttribute("neighbourhoods", locations)
            }
            return places
        } catch (e: Throwable) {
            LOGGER.warn("Failed to load neighbourhoods of city ${city.id}", e)
            return emptyList()
        }
    }
}
