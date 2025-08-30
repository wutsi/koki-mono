package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingStatus
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
@RequestMapping("/listings/status/closed")
@RequiresPermission(["listing:manage", "listing:full_access"])
class StatusClosedListingController : AbstractListingController() {
    @GetMapping
    fun sold(@RequestParam id: Long, @RequestParam status: ListingStatus, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
                listingType = listing.listingType,
                status = status,
                country = listing.address?.city?.country,
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_STATUS_SOLD,
                title = getMessage("page.listing.status.meta.title"),
            )
        )
        return "listings/status-closed"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/status/done?id=${form.id}&status=${form.status}"
    }
}
