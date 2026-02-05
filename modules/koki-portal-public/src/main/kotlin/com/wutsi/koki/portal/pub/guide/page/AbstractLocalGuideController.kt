package com.wutsi.koki.portal.pub.guide.page

import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.agent.model.AgentMetricModel
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.listing.model.ListingMetricModel
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.place.service.PlaceService
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.whatsapp.service.WhatsappService
import com.wutsi.koki.refdata.dto.LocationType
import org.slf4j.LoggerFactory
import org.springframework.ui.Model

abstract class AbstractLocalGuideController(
    protected val locationService: LocationService,
    protected val listingService: ListingService,
    protected val agentService: AgentService,
    protected val placeService: PlaceService,
    protected val whatsapp: WhatsappService,
) : AbstractPageController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AbstractLocalGuideController::class.java)
    }

    protected fun loadActiveListings(
        name: String,
        locationId: Long,
        listingType: ListingType,
        model: Model,
    ): List<ListingModel> {
        try {
            val listings = listingService.search(
                locationIds = listOf(locationId),
                statuses = listOf(ListingStatus.ACTIVE, ListingStatus.ACTIVE_WITH_CONTINGENCIES),
                listingType = listingType,
                sortBy = ListingSort.RECOMMENDED,
                limit = 20,
            )
            if (!listings.isEmpty()) {
                model.addAttribute("${name}Listings", listings.items)
                model.addAttribute("${name}MoreUrl", "/search?location-id=$locationId&listing-type=$listingType")
            }
            return listings.items
        } catch (ex: Throwable) {
            LOGGER.warn("Failed to load ACTIVE listings", ex)
            return emptyList()
        }
    }

    protected fun loadAgents(location: LocationModel, model: Model): List<AgentModel> {
        try {
            val metrics = listingService.metrics(
                cityId = if (location.type == LocationType.CITY) location.id else null,
                neighbourhoodId = if (location.type == LocationType.NEIGHBORHOOD) location.id else null,
                listingStatus = ListingStatus.ACTIVE,
                dimension = ListingMetricDimension.SELLER_AGENT,
            ).sortedByDescending { metric -> metric.total }

            val agentUserIds = metrics.mapNotNull { metric -> metric.sellerAgentUserId }.take(6)
            if (agentUserIds.isEmpty()) {
                return emptyList()
            }

            val agents = agentService.search(
                userIds = agentUserIds,
                limit = agentUserIds.size,
            ).map { agent ->
                applyMetrics(agent, metrics)
            }

            if (agents.isNotEmpty()) {
                model.addAttribute("agents", agents)

                val topAgent = agents[0]
                model.addAttribute("topAgent", topAgent)
                model.addAttribute(
                    "messageUrl",
                    when (location.type) {
                        LocationType.NEIGHBORHOOD -> whatsapp.toNeighbourhoodUrl(location, topAgent)
                        LocationType.CITY -> whatsapp.toCityUrl(location, topAgent)
                        else -> null
                    }
                )
            }
            return agents
        } catch (ex: Throwable) {
            LOGGER.warn("Failed to load agents for location ${location.id}", ex)
            return emptyList()
        }
    }

    private fun applyMetrics(agent: AgentModel, metrics: List<ListingMetricModel>): AgentModel {
        val sale =
            metrics.find { metric -> metric.sellerAgentUserId == agent.user.id && metric.listingType == ListingType.SALE }
                ?.let { metric -> AgentMetricModel(metric.total, metric.averagePrice) }
        val rental =
            metrics.find { metric -> metric.sellerAgentUserId == agent.user.id && metric.listingType == ListingType.RENTAL }
                ?.let { metric -> AgentMetricModel(metric.total, metric.averagePrice) }

        return agent.copy(
            activeSaleMetric = sale,
            activeRentalMetric = rental,
        )
    }

    fun loadPlace(location: LocationModel, model: Model): PlaceModel? {
        try {
            val placeId = placeService.search(
                cityIds = if (location.type == LocationType.CITY) listOf(location.id) else emptyList(),
                neighbourhoodIds = if (location.type == LocationType.NEIGHBORHOOD) listOf(location.id) else emptyList(),
                types = when (location.type) {
                    LocationType.CITY -> listOf(PlaceType.CITY)
                    LocationType.NEIGHBORHOOD -> listOf(PlaceType.NEIGHBORHOOD)
                    else -> emptyList()
                },
                statuses = listOf(PlaceStatus.PUBLISHED),
                limit = 1,
            ).firstOrNull()?.id

            if (placeId != null) {
                val place = placeService.get(placeId)
                model.addAttribute("place", place)
                return place
            }
            return null
        } catch (ex: Exception) {
            LOGGER.warn("Failed to load the place for $location", ex)
            return null
        }
    }

    protected fun loadPriceTrendMetrics(city: LocationModel, model: Model) {
        try {
            // Per categories
            val metrics = listingService.metrics(
                cityId = city.id,
                listingStatus = ListingStatus.ACTIVE,
                dimension = ListingMetricDimension.PROPERTY_CATEGORY,
            )
            model.addAttribute(
                "overallRentalMetrics",
                listingService.sum(metrics.filter { metric -> metric.listingType == ListingType.RENTAL })

            )
            model.addAttribute(
                "overallSalesMetrics",
                listingService.sum(metrics.filter { metric -> metric.listingType == ListingType.SALE }),
            )
            model.addAttribute(
                "rentalMetrics",
                metrics.find { metric -> metric.propertyCategory == PropertyCategory.RESIDENTIAL && metric.listingType == ListingType.RENTAL },
            )
            model.addAttribute(
                "salesMetrics",
                metrics.find { metric -> metric.propertyCategory == PropertyCategory.RESIDENTIAL && metric.listingType == ListingType.SALE },
            )
            model.addAttribute(
                "landMetrics",
                metrics.find { metric -> metric.propertyCategory == PropertyCategory.LAND && metric.listingType == ListingType.SALE },
            )

            // Per room
            val metricsPerRoom = listingService.metrics(
                cityId = city.id,
                listingStatus = ListingStatus.ACTIVE,
                propertyCategory = PropertyCategory.RESIDENTIAL,
                dimension = ListingMetricDimension.BEDROOMS,
            ).sortedBy { metric -> metric.bedrooms ?: 0 }
            model.addAttribute(
                "rentalMetricsPerRoom",
                metricsPerRoom.filter { metric -> metric.listingType == ListingType.RENTAL }.take(5)
            )
            model.addAttribute(
                "salesMetricsPerRoom",
                metricsPerRoom.filter { metric -> metric.listingType == ListingType.SALE }.take(5)
            )
        } catch (e: Throwable) {
            LOGGER.warn("Unable to load metrics for city ${city.id}", e)
        }
    }

    protected fun loadPointOfInterests(location: LocationModel, model: Model): List<PlaceModel> {
        try {
            val places = placeService.search(
                cityIds = if (location.type == LocationType.CITY) listOf(location.id) else emptyList(),
                neighbourhoodIds = if (location.type == LocationType.NEIGHBORHOOD) listOf(location.id) else emptyList(),
                statuses = listOf(PlaceStatus.PUBLISHED),
                types = listOf(
                    PlaceType.SCHOOL,
                    PlaceType.PARK,
                    PlaceType.MUSEUM,
                    PlaceType.HOSPITAL,
                    PlaceType.MARKET,
                    PlaceType.SUPERMARKET,
                ),
                limit = 50,
            ).sortedByDescending { school -> school.rating ?: 0.0 }

            filterPlaces("schools", listOf(PlaceType.SCHOOL), places, model)
            filterPlaces("hospitals", listOf(PlaceType.HOSPITAL), places, model)
            filterPlaces("markets", listOf(PlaceType.MARKET, PlaceType.SUPERMARKET), places, model)
            filterPlaces("todos", listOf(PlaceType.PARK, PlaceType.MUSEUM), places, model)
            return places
        } catch (ex: Exception) {
            LOGGER.warn("Failed to load neighbourhood places", ex)
            return emptyList()
        }
    }

    private fun filterPlaces(name: String, types: List<PlaceType>, places: List<PlaceModel>, model: Model) {
        val items = places.filter { types.contains(it.type) }
            .sortedByDescending { it.rating ?: 0.0 }
            .take(5)

        if (items.isNotEmpty()) {
            model.addAttribute(name, items)
        }
    }
}
