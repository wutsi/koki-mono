package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.webscaping.service.WebpageService
import com.wutsi.koki.portal.webscaping.service.WebsiteService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/debug")
@RequiresPermission(["tenant:debug"])
class ListingDebugController(
    private val webpageService: WebpageService,
    private val websiteService: WebsiteService,
) : AbstractListingDetailsController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListingDebugController::class.java)
    }

    @GetMapping
    fun debug(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)

        loadAIListing(listing, model)
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

    private fun loadAIListing(listing: ListingModel, model: Model) {
        try {
            val aiListing = listingService.getAIListing(listing.id)
            model.addAttribute("aiListing", aiListing)
        } catch (ex: Exception) {
            LOGGER.warn("Could not load ai listing for id ${listing.id}", ex)
        }
    }

    private fun loadWebpage(listing: ListingModel, model: Model) {
        val webpageId = webpageService.search(
            listingId = listing.id,
            limit = 1,
        ).firstOrNull()?.id ?: return

        val webpage = webpageService.get(webpageId)
        model.addAttribute("webpage", webpage)

        val website = websiteService.get(webpage.websiteId)
        model.addAttribute("website", website)
    }
}
