package com.wutsi.koki.portal.pub.agent.page

import com.wutsi.koki.listing.dto.ListingMetricDimension
import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyCategory
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.refdata.model.GeoLocationModel
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import com.wutsi.koki.portal.pub.whatsapp.service.WhatsappService
import com.wutsi.koki.refdata.dto.LocationType
import io.hypersistence.utils.common.LogUtils.LOGGER
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/agents")
class AgentController(
    private val agentService: AgentService,
    private val listingService: ListingService,
    private val locationService: LocationService,
    private val whatsapp: WhatsappService,
) : AbstractPageController() {
    companion object {
        const val TOAST_TIMEOUT_MILLIS = 60 * 1000L
        const val TOAST_MESSAGE_SENT = "msg-sent"
    }

    @GetMapping("/{id}/{slug}")
    fun show(
        @PathVariable id: Long,
        @PathVariable slug: String,
        @RequestParam(name = "_toast", required = false) toast: String? = null,
        @RequestParam(name = "_ts", required = false) timestamp: Long? = null,
        model: Model,
    ): String {
        return show(id, toast, timestamp, model)
    }

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        @RequestParam(name = "_toast", required = false) toast: String? = null,
        @RequestParam(name = "_ts", required = false) timestamp: Long? = null,
        model: Model,
    ): String {
        val tenant = tenantHolder.get()
        val agent = agentService.get(id)
        model.addAttribute("agent", agent)

        val rental = loadActiveListings("rental", ListingType.RENTAL, agent, model)
        val sold = loadActiveListings("sale", ListingType.SALE, agent, model)
        loadSoldListings(agent, model)
        loadNeighborhoods(rental + sold, model)

        loadPriceTrendMetrics(agent, model)
        loadToast(toast, timestamp, model)

        model.addAttribute("messageUrl", whatsapp.toAgentUrl(agent))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT,
                title = getMessage(
                    key = "page.agent.show.meta.title",
                    args = arrayOf(agent.user.displayName ?: "", agent.user.city?.name ?: "-", tenant.name)
                ),
                url = agent.publicUrl,
                description = getMessage(
                    key = "page.agent.show.meta.description",
                    args = arrayOf(
                        (agent.user.displayName ?: ""),
                        (agent.user.city?.name ?: ""),
                        (agent.user.city?.countryName ?: "")
                    )
                )
            )
        )
        return "agents/show"
    }

    private fun loadActiveListings(
        prefix: String,
        listingType: ListingType,
        agent: AgentModel,
        model: Model
    ): List<ListingModel> {
        val listings = listingService.search(
            sellerAgentUserId = agent.user.id,
            listingType = listingType,
            statuses = listOf(
                ListingStatus.ACTIVE,
                ListingStatus.ACTIVE_WITH_CONTINGENCIES,
            ),
            sortBy = ListingSort.NEWEST,
            limit = 100,
        ).items
        if (listings.isNotEmpty()) {
            model.addAttribute("${prefix}Listings", listings.take(20))
        }
        return listings
    }

    private fun loadSoldListings(agent: AgentModel, model: Model): List<ListingModel> {
        val listings = listingService.search(
            agentUserId = agent.user.id,
            statuses = listOf(
                ListingStatus.RENTED,
                ListingStatus.SOLD,
            ),
            sortBy = ListingSort.TRANSACTION_DATE,
            limit = 100,
        ).items
        if (listings.isNotEmpty()) {
            model.addAttribute("soldListings", listings.take(20))
            model.addAttribute("mapCenterPoint", toMapCenterPoint(listings))
            model.addAttribute("mapMarkersJson", toMapMarkersJson(listings))
        }
        return listings
    }

    private fun loadNeighborhoods(listings: List<ListingModel>, model: Model): List<LocationModel> {
        val neighbourhoodIds = listings.mapNotNull { listing -> listing.address?.neighbourhood?.id }
            .distinct()
            .take(12)

        val neighbourhoods = locationService.search(
            ids = neighbourhoodIds,
            types = listOf(LocationType.NEIGHBORHOOD),
            limit = neighbourhoodIds.size,
        )
        model.addAttribute("neighbourhoods", neighbourhoods)
        return neighbourhoods
    }

    private fun toMapCenterPoint(listings: List<ListingModel>): GeoLocationModel? {
        // Listings by neighborhoods
        val listingsByNeighborhood = listings
            .filter { listing -> listing.address?.neighbourhood?.id != null && listing.geoLocation != null }
            .groupBy { listing -> listing.address?.neighbourhood?.id ?: -1 }

        // Top neighborhoods having the most listings
        val topNeighborhoodId = listingsByNeighborhood.keys.maxByOrNull { neighbourhoodId ->
            listingsByNeighborhood[neighbourhoodId]?.size ?: 0
        } ?: -1

        return listings.filter { listing -> listing.geoLocation != null }
            .find { listing -> listing.address?.neighbourhood?.id == topNeighborhoodId }
            ?.geoLocation
    }

    private fun loadToast(
        toast: String? = null,
        timestamp: Long? = null,
        model: Model,
    ) {
        if (toast == null || timestamp == null) {
            return
        }
        if (System.currentTimeMillis() - timestamp > TOAST_TIMEOUT_MILLIS) {
            return
        }

        val message = when (toast) {
            TOAST_MESSAGE_SENT -> getMessage("page.agent.toast.message-sent")
            else -> null
        }
        model.addAttribute("toastMessage", message)
    }

    private fun loadPriceTrendMetrics(agent: AgentModel, model: Model) {
        val userId = agent.user.id
        try {
            // Per categories
            val metrics = listingService.metrics(
                sellerAgentUserIds = listOf(userId),
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
                sellerAgentUserIds = listOf(userId),
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
            LOGGER.warn("Unable to load price trend metrics for agent: ${agent.id}", e)
        }
    }
}
