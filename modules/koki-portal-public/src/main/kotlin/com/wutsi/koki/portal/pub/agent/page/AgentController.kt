package com.wutsi.koki.portal.pub.agent.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.refdata.model.GeoLocationModel
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
        val listings = activeListings + soldListings
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

    fun toMapCenterPoint(listings: List<ListingModel>): GeoLocationModel? {
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
}
