package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.listing.page.AbstractListingController.Companion.loadPriceTrendMetrics
import com.wutsi.koki.portal.offer.service.OfferService
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import com.wutsi.koki.portal.webscaping.service.WebpageService
import com.wutsi.koki.refdata.dto.CategoryType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings")
@RequiresPermission(["listing", "listing:full_access"])
class ListingController(
    private val categoryService: CategoryService,
    private val offerService: OfferService,
    private val websiteService: WebpageService,
) : AbstractListingDetailsController() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ListingController::class.java)
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)

        // Offers
        loadOfferCount(listing, model)

        // Webpage info
        loadWebpage(listing, model)

        // Message URL
        val user = getUser()
        val toggles = getToggles()
        if (
            toggles.modules.message &&
            listing.status != ListingStatus.DRAFT &&
            listing.sellerAgentUser != null &&
            listing.sellerAgentUser.id != user?.id
        ) {
            val userId = listing.sellerAgentUser.id
            val type = ObjectType.LISTING
            model.addAttribute("composeUrl", "/messages/compose?to-user-id=$userId&owner-id=$id&owner-type=$type")
        }

        // Tab to exclude
        val excludedTabs = listOfNotNull(
            if (canViewMessageTab(listing)) null else "message",
            if (canViewOfferTab(listing, user)) null else "offer",
            if (canViewLeadTab(listing)) null else "lead",
        )
        model.addAttribute("excludedTabs", excludedTabs)

        // Price trend
        loadPriceTrendMetrics(listing, model, listingService)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING,
                title = getMessage("page.listing.show.meta.title", arrayOf(listing.listingNumber)),
            )
        )
        return "listings/show"
    }

    @GetMapping("/tab/details")
    fun details(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("amenityCategories", findAmenityCategories())
        return "listings/details"
    }

    @PostMapping("/generate-qr-code")
    fun generateQrCode(@RequestParam id: Long): String {
        listingService.generateQrCode(id)
        return "redirect:/listings/$id?tab=qr-code"
    }

    private fun loadWebpage(listing: ListingModel, model: Model) {
        try {
            val webpage = websiteService.search(
                listingId = listing.id,
                limit = 1,
            ).firstOrNull()

            model.addAttribute("webpage", webpage)
        } catch (e: Exception) {
            LOGGER.warn("Unable to load webpage for listing=${listing.id}", e)
        }
    }

    private fun loadOfferCount(listing: ListingModel, model: Model) {
        if (!listing.statusActive || !getToggles().modules.offer) {
            return
        }

        val offers = offerService.search(
            ownerId = listing.id,
            ownerType = ObjectType.LISTING,
            statuses = listOf(OfferStatus.SUBMITTED),
            limit = 10,
            fullGraph = false,
        )
        if (offers.isNotEmpty()) {
            model.addAttribute("totalActiveOffers", offers.size)
        }
    }

    private fun findAmenityCategories(): List<CategoryModel> {
        return categoryService.search(
            limit = Integer.MAX_VALUE,
            type = CategoryType.AMENITY,
        )
    }

    private fun canViewOfferTab(listing: ListingModel, user: UserModel?): Boolean {
        if (listing.statusDraft) {
            return false
        }

        if (user?.hasFullAccess("offer") == true) {
            return true
        } else {
            return listing.sellerAgentUser?.id == user?.id
        }
    }

    private fun canViewMessageTab(listing: ListingModel): Boolean {
        return !listing.statusDraft
    }

    private fun canViewLeadTab(listing: ListingModel): Boolean {
        return !listing.statusDraft
    }
}
