package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.common.page.AbstractPageController
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.service.ListingService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/widgets")
class ListingWidgetController(
    private val service: ListingService
) : AbstractPageController() {
    @GetMapping("/recent")
    fun recent(@RequestParam(name = "test-mode", required = false) testMode: Boolean? = null, model: Model): String {
        val listings = findListings(
            statuses = listOf(ListingStatus.ACTIVE),
            sortBy = ListingSort.NEWEST,
        )
        if (!listings.isEmpty()) {
            model.addAttribute("title", getMessage("carousel.listing.recent.title"))
            model.addAttribute("listings", listings)
        }
        model.addAttribute("testMode", testMode)
        return "listings/widgets/carousel"
    }

    @GetMapping("/sold")
    fun sold(model: Model): String {
        val listings = findListings(
            statuses = listOf(ListingStatus.SOLD, ListingStatus.RENTED),
            sortBy = ListingSort.TRANSACTION_DATE,
        )
        if (!listings.isEmpty()) {
            model.addAttribute("title", getMessage("carousel.listing.sold.title"))
            model.addAttribute("listings", listings)
        }
        return "listings/widgets/carousel"
    }

    private fun findListings(
        statuses: List<ListingStatus>,
        sortBy: ListingSort
    ): List<ListingModel> {
        val city = userHolder.get()?.city ?: resolveCity()
        val listings = if (city != null) {
            service.search(
                locationIds = city.let { city -> listOf(city.id) } ?: emptyList(),
                statuses = statuses,
                sortBy = sortBy
            ).items.toMutableList()
        } else {
            mutableListOf()
        }

        if (listings.size < 5) {
            val excludeIds = listings.map { listing -> listing.id }
            listings.addAll(
                service.search(
                    statuses = statuses,
                    sortBy = sortBy
                ).items.filter { listing -> !excludeIds.contains(listing.id) }
            )
        }
        return listings
    }
}
