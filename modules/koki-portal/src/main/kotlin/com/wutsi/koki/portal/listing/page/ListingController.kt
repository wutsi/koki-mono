package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.refdata.model.CategoryModel
import com.wutsi.koki.portal.refdata.service.CategoryService
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.CategoryType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings")
@RequiresPermission(["listing", "listing:full_access"])
class ListingController(
    private val categoryService: CategoryService
) : AbstractListingDetailsController() {
    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)

        val user = getUser()
        if (
            listing.status != ListingStatus.DRAFT &&
            listing.sellerAgentUser != null &&
            listing.sellerAgentUser.id != user?.id
        ) {
            val userId = listing.sellerAgentUser.id
            val type = ObjectType.LISTING
            model.addAttribute("composeUrl", "/messages/compose?to-user-id=$userId&owner-id=$id&owner-type=$type")
        }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING,
                title = getMessage("page.listing.show.meta.title", arrayOf(listing.listingNumber)),
            )
        )

        if (listing.statusDraft) {
            model.addAttribute(
                "excludedTabs",
                listOf(
                    "message"
                )
            )
        }

        return "listings/show"
    }

    @GetMapping("/tab/details")
    fun details(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("amenityCategories", findAmenityCategories())
        return "listings/details"
    }

    private fun findAmenityCategories(): List<CategoryModel> {
        return categoryService.search(
            limit = Integer.MAX_VALUE,
            type = CategoryType.AMENITY,
        )
    }
}
