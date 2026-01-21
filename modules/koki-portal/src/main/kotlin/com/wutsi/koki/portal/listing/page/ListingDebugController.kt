package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.webscaping.service.WebpageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/debug")
@RequiresPermission(["listing", "listing:full_access"])
class ListingDebugController(
    private val websiteService: WebpageService,
) : AbstractListingDetailsController() {
    @GetMapping
    fun debug(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)

        loadWebpage(listing, model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_DEBUG,
                title = "Debug Listing #${listing.id}",
            )
        )
        return "listings/debug"
    }

    private fun loadWebpage(listing: ListingModel, model: Model) {
        val webpageId = websiteService.search(
            listingId = listing.id,
            limit = 1,
        ).firstOrNull()?.id ?: return

        val webpage = websiteService.get(webpageId)
        model.addAttribute("webpage", webpage)
    }
}
