package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/status")
@RequiresPermission(["listing:manage", "listing:full_access"])
class StatusListingController : AbstractListingController() {
    @GetMapping
    fun status(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
                status = listing.status,
            )
        )

        model.addAttribute(
            "statusesOnMarket",
            ListingStatus.entries.filter { status -> isOnMarket(status) }
        )

        model.addAttribute(
            "statusesOffMarket",
            ListingStatus.entries.filter { status -> isOffMarket(status) }
                .filter { status ->
                    (status == ListingStatus.RENTED && listing.listingType == ListingType.RENTAL) ||
                        (status == ListingStatus.SOLD && listing.listingType == ListingType.SALE) ||
                        (status != ListingStatus.SOLD && status != ListingStatus.RENTED)
                }
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_STATUS,
                title = getMessage("page.listing.status.meta.title"),
            )
        )
        return "listings/status"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        if (isSuccessful(form.status)) {
            return "redirect:/listings/status/sold?id=${form.id}&status=${form.status}"
        } else if (isOffMarket(form.status)) {
            return "redirect:/listings/status/closed?id=${form.id}&status=${form.status}"
        } else {
            return "redirect:/listings/status/done?id=${form.id}&status=${form.status}"
        }
    }

    private fun isSuccessful(status: ListingStatus): Boolean {
        return status == ListingStatus.RENTED ||
            status == ListingStatus.SOLD
    }

    private fun isOnMarket(status: ListingStatus): Boolean {
        return status == ListingStatus.ACTIVE ||
            status == ListingStatus.ACTIVE_WITH_OFFER ||
            status == ListingStatus.PENDING
    }

    private fun isOffMarket(status: ListingStatus): Boolean {
        return status == ListingStatus.RENTED ||
            status == ListingStatus.SOLD ||
            status == ListingStatus.EXPIRED ||
            status == ListingStatus.WITHDRAWN ||
            status == ListingStatus.CANCELLED
    }
}
