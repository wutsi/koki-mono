package com.wutsi.koki.portal.pub.agent.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.refdata.model.LocationModel
import com.wutsi.koki.refdata.dto.LocationType
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
        val agent = agentService.get(id)
        model.addAttribute("agent", agent)

        val activeListings = loadActiveListings(agent, model)
        val soldListings = findSoldListings(agent)
        val listings = soldListings + activeListings
        if (listings.isNotEmpty()) {
            model.addAttribute("listings", listings)
            model.addAttribute("mapCenterPoint", toMapCenterPoint(listings))
            model.addAttribute("mapMarkersJson", toMapMarkersJson(listings))
        }

        loadToast(toast, timestamp, model)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT,
                title = agent.user.displayName ?: "",
                description = agent.user.biography,
                url = agent.publicUrl,
            )
        )
        return "agents/show"
    }

    private fun loadActiveListings(agent: AgentModel, model: Model): List<ListingModel> {
        val listings = listingService.search(
            agentUserId = agent.user.id,
            statuses = listOf(
                ListingStatus.ACTIVE,
                ListingStatus.ACTIVE_WITH_CONTINGENCIES,
            ),
            sortBy = ListingSort.NEWEST,
            limit = 20,
        ).items
        if (listings.isNotEmpty()) {
            model.addAttribute("activeListings", listings)
        }
        return listings
    }

    private fun findSoldListings(agent: AgentModel): List<ListingModel> {
        return listingService.search(
            agentUserId = agent.user.id,
            statuses = listOf(
                ListingStatus.RENTED,
                ListingStatus.SOLD,
            ),
            sortBy = ListingSort.TRANSACTION_DATE,
            limit = 100,
        ).items
    }

    private fun toMapMarkersJson(listings: List<ListingModel>): String? {
        val beds = getMessage("page.listing.bedrooms-abbreviation")
        val markers = listings
            .filter { listing -> listing.geoLocation != null }
            .map { listing ->
                mapOf(
                    "id" to listing.id,
                    "rental" to (listing.listingType == ListingType.RENTAL),
                    "sold" to (listing.status == ListingStatus.RENTED || listing.status == ListingStatus.SOLD),
                    "latitude" to listing.geoLocation?.latitude,
                    "longitude" to listing.geoLocation?.longitude,
                    "location" to listOfNotNull(listing.address?.neighbourhood?.name, listing.address?.city?.name)
                        .joinToString(", "),
                    "price" to if (listing.statusSold) listing.salePrice?.displayText else listing.price?.displayText,
                    "heroImageUrl" to listing.heroImageUrl,
                    "bedrooms" to (listing.bedrooms?.toString() ?: "--") + " " + beds,
                    "area" to ((listing.lotArea?.let { listing.propertyArea }?.toString() ?: "--") + "m2"),
                    "url" to listing.publicUrl,
                    "status" to if (listing.status == ListingStatus.RENTED) {
                        getMessage("listing-status.RENTED") + " " + listing.soldAtText
                    } else if (listing.status == ListingStatus.SOLD) {
                        getMessage("listing-status.SOLD") + " " + listing.soldAtText
                    } else {
                        ""
                    }
                )
            }
        return jsonMapper.writeValueAsString(markers)
    }

    fun toMapCenterPoint(listings: List<ListingModel>): LocationModel? {
        val locations =
            listings.flatMap { listing -> listOf(listing.address?.city, listing.address?.neighbourhood) }
                .filterNotNull()
                .filter { location -> location.latitude != null && location.longitude != null }
                .distinctBy { location -> location.id }

        val locationCount =
            listings.flatMap { listing -> listOf(listing.address?.city, listing.address?.neighbourhood) }
                .filterNotNull()
                .filter { location -> location.latitude != null && location.longitude != null }
                .groupBy { city -> city.id }

        val sorted = locations.sortedBy { location -> locationCount[location.id]?.size ?: 0 }

        return sorted.firstOrNull { location -> location.type == LocationType.NEIGHBORHOOD }
            ?: sorted.firstOrNull()
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
}
