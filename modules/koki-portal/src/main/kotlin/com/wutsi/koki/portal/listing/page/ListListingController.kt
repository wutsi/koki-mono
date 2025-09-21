package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingSort
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.common.model.ResultSetModel
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings")
@RequiresPermission(["listing", "listing:full_access"])
class ListListingController : AbstractListingController() {
    @GetMapping
    fun list(@RequestParam(required = false) filter: Int = 0, model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_LIST,
                title = getMessage("page.listing.list.meta.title"),
            )
        )

        model.addAttribute("filterUrl", "/listings/search/filter")
        model.addAttribute("filter", filter)
        more(filter, 20, 0, model)
        return "listings/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(required = false) filter: Int = 0,
        @RequestParam(required = false) limit: Int = 20,
        @RequestParam(required = false) offset: Int = 0,
        model: Model,
    ): String {
        val listings = findListings(filter, limit, offset)

        if (listings.items.isNotEmpty()) {
            model.addAttribute("listings", listings)
            if (listings.items.size >= limit) {
                model.addAttribute(
                    "moreUrl",
                    "/listings/more?filter=$filter&limit=$limit&offset=" + (offset + limit),
                )
            }
        }
        model.addAttribute("sold", filter == 1)
        model.addAttribute("showAgent", false)
        return "listings/more"
    }

    private fun findListings(filter: Int?, limit: Int, offset: Int): ResultSetModel<ListingModel> {
        val userId = userHolder.get()?.id
        val listings = when (filter) {
            1 -> listingService.search(
                statuses = listOf(ListingStatus.SOLD, ListingStatus.RENTED),
                agentUserId = userId,
                limit = limit,
                offset = offset,
                sortBy = ListingSort.TRANSACTION_DATE,
            )

            2 -> listingService.search(
                statuses = listOf(
                    ListingStatus.WITHDRAWN,
                    ListingStatus.CANCELLED,
                    ListingStatus.EXPIRED,
                ),
                sellerAgentUserId = userId,
                limit = limit,
                offset = offset,
                sortBy = ListingSort.NEWEST,
            )

            else -> listingService.search(
                statuses = listOf(
                    ListingStatus.DRAFT,
                    ListingStatus.PUBLISHING,
                    ListingStatus.ACTIVE,
                    ListingStatus.ACTIVE_WITH_CONTINGENCIES,
                    ListingStatus.PENDING,
                ),
                sellerAgentUserId = userId,
                limit = limit,
                offset = offset,
                sortBy = ListingSort.NEWEST,
            )
        }
        return listings
    }
}
