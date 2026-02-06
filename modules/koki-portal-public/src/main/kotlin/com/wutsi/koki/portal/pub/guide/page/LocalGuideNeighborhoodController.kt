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
@RequestMapping("/local-guides/neighbourhoods")
class LocalGuideNeighborhoodController(
    locationService: LocationService,
    listingService: ListingService,
    agentService: AgentService,
    placeService: PlaceService,
    whatsapp: WhatsappService,
) : AbstractLocalGuideController(locationService, listingService, agentService, placeService, whatsapp) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(LocalGuideNeighborhoodController::class.java)
    }

    @GetMapping("/{id}/{slug}")
    fun show(@PathVariable id: Long, @PathVariable slug: String, model: Model): String {
        return show(id, model)
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        // Neighbourhood
        val neighbourhood = locationService.get(id, LocationType.NEIGHBORHOOD)
        model.addAttribute("neighbourhood", neighbourhood)

        val city = neighbourhood.parentId?.let { id -> locationService.get(id) }
        model.addAttribute("city", city)

        // Places
        val place = loadPlace(neighbourhood, model)
        if (place != null) {
            loadSimilarNeighborhoods(place, model)
        }

        // Points of interest
        loadPointOfInterests(neighbourhood, model)

        // Listings
        loadActiveListings("rental", neighbourhood.id, ListingType.RENTAL, model)
        loadActiveListings("sale", neighbourhood.id, ListingType.SALE, model)

        // Agents
        val agents = loadAgents(neighbourhood, model)

        // Price Trends
        loadPriceTrendMetrics(neighbourhood, model)

        val tenant = tenantHolder.get()
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LOCAL_GUIDE_NEIGHBOURHOOD,
                title = getMessage(
                    "page.local-guide.neighbourhood.show.meta.title",
                    arrayOf(neighbourhood.name, city?.name ?: "", tenant.name),
                ),
                url = neighbourhood.publicUrl,
                description = place?.summary,
                image = place?.heroImageUrl,
            ),
        )
        return "local-guides/neighbourhood"
    }

    private fun loadSimilarNeighborhoods(place: PlaceModel, model: Model): List<PlaceModel> {
        try {
            val rating = place.rating ?: 0.0
            val minRating = if (rating.toInt() >= 4) 4.0 else rating - .25
            val maxRating = if (rating.toInt() >= 4) null else rating + .25

            val places = placeService.search(
                cityIds = listOf(place.cityId),
                types = listOf(PlaceType.NEIGHBORHOOD),
                statuses = listOf(PlaceStatus.PUBLISHED),
                minRating = minRating,
                maxRating = maxRating,
                sort = PlaceSort.RATING_HIGH_LOW,
                limit = 12,
            ).filter { it.id != place.id }
            if (places.isNotEmpty()) {
                val neighbourhoodIds = places.mapNotNull { place -> place.neighbourhoodId }
                val neighbourhoods = locationService.search(
                    ids = neighbourhoodIds,
                    types = listOf(LocationType.NEIGHBORHOOD),
                    limit = neighbourhoodIds.size,
                )
                if (neighbourhoods.isNotEmpty()) {
                    model.addAttribute("similarNeighbourhoods", neighbourhoods)
                }
            }
            return places
        } catch (e: Throwable) {
            LOGGER.warn("Failed to load similar neighbourhoods", e)
            return emptyList()
        }
    }
}
