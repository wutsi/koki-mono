package com.wutsi.koki.portal.agent.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.agent.model.AgentModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.service.ListingService
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.LocationType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/agents")
@RequiresPermission(["agent", "agent:full_access"])
class AgentController(
    private val listingService: ListingService
) : AbstractAgentController() {
    @GetMapping("/{id}")
    fun list(@PathVariable id: Long, model: Model): String {
        val agent = findAgent(id)
        model.addAttribute("agent", agent)

        val activeListings = findActiveListings(agent)
        val soldListings = findSoldListings(agent)
        model.addAttribute("activeListings", activeListings)
        model.addAttribute("soldListings", soldListings)

        val listings = activeListings + soldListings
        model.addAttribute("listings", listings)
        model.addAttribute("mapCenterPoint", toMapCenterPoint(listings))
        model.addAttribute("mapMarkersJson", toMapMarkersJson(listings))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.AGENT,
                title = agent.user.displayName ?: "",
            )
        )
        return "agents/show"
    }

    private fun findAgent(id: Long): AgentModel {
        return agentService.get(id)
    }

    private fun findActiveListings(agent: AgentModel): List<ListingModel> {
        return listingService.search(
            agentUserId = agent.user.id,
            statuses = listOf(
                ListingStatus.ACTIVE,
                ListingStatus.ACTIVE_WITH_CONTINGENCIES,
            ),
            sortBy = ListingSort.NEWEST,
            limit = 20,
        ).items
    }

    private fun findSoldListings(agent: AgentModel): List<ListingModel> {
        return listingService.search(
            agentUserId = agent.user.id,
            statuses = listOf(
                ListingStatus.RENTED,
                ListingStatus.SOLD,
            ),
            sortBy = ListingSort.TRANSACTION_DATE,
            limit = 20,
        ).items
    }

    private fun toMapMarkersJson(listings: List<ListingModel>): String? {
        val beds = getMessage("page.listing.bedrooms-abbreviation")
        val markers = listings
            .filter { listing -> listing.geoLocation != null }
            .map { listing ->
                mapOf(
                    "id" to listing.id,
                    "sold" to (listing.status == ListingStatus.RENTED || listing.status == ListingStatus.SOLD),
                    "rental" to (listing.listingType == ListingType.RENTAL),
                    "latitude" to listing.geoLocation?.latitude,
                    "longitude" to listing.geoLocation?.longitude,
                    "location" to listOfNotNull(listing.address?.neighbourhood?.name, listing.address?.city?.name)
                        .joinToString(", "),
                    "price" to if (listing.statusSold) listing.salePrice?.displayText else listing.price?.displayText,
                    "heroImageUrl" to listing.heroImageUrl,
                    "bedrooms" to (listing.bedrooms?.toString() ?: "--") + " " + beds,
                    "area" to ((listing.lotArea?.let { listing.propertyArea }?.toString() ?: "--") + "m2"),
                    "url" to "/listings/${listing.id}",
                    "status" to if (listing.status == ListingStatus.RENTED) {
                        getMessage("listing-status.RENTED") + " " + listing.soldAtText
                    } else if (listing.status == ListingStatus.SOLD) {
                        getMessage("listing-status.SOLD") + " " + listing.soldAtText
                    } else {
                        ""
                    }
                )
            }
        return objectMapper.writeValueAsString(markers)
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
}
