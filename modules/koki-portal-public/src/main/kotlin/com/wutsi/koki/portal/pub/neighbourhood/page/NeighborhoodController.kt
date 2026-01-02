package com.wutsi.koki.portal.pub.neighbourhood.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.place.dto.PlaceStatus
import com.wutsi.koki.place.dto.PlaceType
import com.wutsi.koki.portal.pub.agent.model.AgentModel
import com.wutsi.koki.portal.pub.agent.service.AgentService
import com.wutsi.koki.portal.pub.common.mapper.MoneyMapper
import com.wutsi.koki.portal.pub.common.page.AbstractPageController
import com.wutsi.koki.portal.pub.common.page.PageName
import com.wutsi.koki.portal.pub.common.util.MoneyUtil
import com.wutsi.koki.portal.pub.listing.model.ListingModel
import com.wutsi.koki.portal.pub.listing.service.ListingService
import com.wutsi.koki.portal.pub.place.model.PlaceModel
import com.wutsi.koki.portal.pub.place.service.PlaceService
import com.wutsi.koki.portal.pub.refdata.service.LocationService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/neighbourhoods")
class NeighborhoodController(
    private val locationService: LocationService,
    private val listingService: ListingService,
    private val agentService: AgentService,
    private val placeService: PlaceService,
    private val moneyMapper: MoneyMapper,
) : AbstractPageController() {

    @GetMapping("/{id}/{slug}")
    fun show(@PathVariable id: Long, @PathVariable slug: String, model: Model): String {
        return show(id, model)
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val neighbourhood = locationService.get(id)
        model.addAttribute("neighbourhood", neighbourhood)

        val city = neighbourhood.parentId?.let { id -> locationService.get(id) }
        model.addAttribute("city", city)

        val rentals = loadActiveListings("rental", neighbourhood.id, ListingType.RENTAL, model)
        val sales = loadActiveListings("sale", neighbourhood.id, ListingType.SALE, model)
        val sold = loadSoldListings(neighbourhood.id, model)
        if (sold.isNotEmpty()) {
            loadMap(sold, model)
        }
        val all = rentals + sales + sold
        if (all.isNotEmpty()) {
            loadAgents(all, model)
        }

        loadSchools(neighbourhood.id, model)

        val place = loadPlace(neighbourhood.id, model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.NEIGHBOURHOOD,
                title = neighbourhood.name,
                url = neighbourhood.publicUrl,
                description = place?.summary,
                image = place?.heroImageUrl,
            ),
        )
        return "neighbourhoods/show"
    }

    private fun loadActiveListings(
        name: String,
        neighbourhoodId: Long,
        listingType: ListingType,
        model: Model,
    ): List<ListingModel> {
        val listings = listingService.search(
            locationIds = listOf(neighbourhoodId),
            statuses = listOf(ListingStatus.ACTIVE, ListingStatus.ACTIVE_WITH_CONTINGENCIES),
            listingType = listingType,
            sortBy = ListingSort.NEWEST,
            limit = 100,
        )
        if (listings.isEmpty()) {
            return emptyList()
        }

        val min = listings.items.mapNotNull { listing -> listing.price }.minBy { price -> price.amount }
        val max = listings.items.mapNotNull { listing -> listing.price }.maxBy { price -> price.amount }
        val sum = listings.items.mapNotNull { listing -> listing.price }.sumOf { price -> price.amount }
        val avg = moneyMapper.toMoneyModel(sum / listings.total, min.currency)

        model.addAttribute("${name}Listings", listings.items.take(20))
        model.addAttribute("${name}Count", listings.total)
        model.addAttribute("${name}AveragePrice", avg.shortText)
        model.addAttribute("${name}PriceRange", MoneyUtil.priceRangeText(min, max))

        return listings.items
    }

    private fun loadSoldListings(
        neighbourhoodId: Long,
        model: Model,
    ): List<ListingModel> {
        val listings = listingService.search(
            locationIds = listOf(neighbourhoodId),
            statuses = listOf(ListingStatus.RENTED, ListingStatus.SOLD),
            sortBy = ListingSort.TRANSACTION_DATE,
            limit = 100,
        )
        if (listings.isEmpty()) {
            return emptyList()
        }

        model.addAttribute("soldListings", listings.items.take(20))
        return listings.items
    }

    private fun loadAgents(listings: List<ListingModel>, model: Model): List<AgentModel> {
        val agentUserIdsMap = listings.filter { listing -> listing.sellerAgentUser != null }
            .groupBy { listing -> listing.sellerAgentUser?.id ?: -1L }

        val topAgentUserIds = listings
            .mapNotNull { listing -> listing.sellerAgentUser?.id }
            .toList()
            .distinct()
            .sortedByDescending { id -> agentUserIdsMap[id]?.size ?: 0 }
            .take(6)
        val agents = agentService.search(
            userIds = topAgentUserIds,
            limit = topAgentUserIds.size,
        )
        model.addAttribute("agents", agents)

        val topAgentUserId = topAgentUserIds[0]
        model.addAttribute("topAgent", agents.find { agent -> agent.user.id == topAgentUserId })
        return agents
    }

    private fun loadMap(listings: List<ListingModel>, model: Model) {
        val markersJson = toMapMarkersJson(listings)

        model.addAttribute("mapMarkersJson", markersJson)
        val centerPoint = listings.firstNotNullOfOrNull { listing -> listing.geoLocation }
        model.addAttribute("mapCenterPoint", centerPoint)
    }

    private fun loadPlace(neighbourhoodId: Long, model: Model): PlaceModel? {
        val item = placeService.search(
            neighbourhoodIds = listOf(neighbourhoodId),
            types = listOf(PlaceType.NEIGHBORHOOD),
            statuses = listOf(PlaceStatus.PUBLISHED),
            limit = 1,
        ).items.firstOrNull() ?: return null

        val place = placeService.get(item.id)
        model.addAttribute("place", place)
        return place
    }

    private fun loadSchools(neighbourhoodId: Long, model: Model): List<PlaceModel> {
        val schools = placeService.search(
            neighbourhoodIds = listOf(neighbourhoodId),
            types = listOf(PlaceType.SCHOOL),
            statuses = listOf(PlaceStatus.PUBLISHED),
            limit = 10,
        ).items.sortedByDescending { school -> school.rating ?: 0.0 }

        if (schools.isNotEmpty()) {
            model.addAttribute("schools", schools)
        }
        return schools
    }
}
