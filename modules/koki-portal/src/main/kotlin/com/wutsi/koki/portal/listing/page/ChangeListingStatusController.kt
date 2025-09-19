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
@RequestMapping("/listings/status")
@RequiresPermission(["listing:manage", "listing:full_access"])
class ChangeListingStatusController : AbstractEditListingController() {
    @GetMapping
    fun status(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", ListingForm(id = id))

        model.addAttribute(
            "statuses",
            mutableListOf(
                ListingStatus.EXPIRED,
                ListingStatus.WITHDRAWN,
                ListingStatus.CANCELLED,
            ),
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
    fun submit(@ModelAttribute form: ListingForm): String {
        return "redirect:/listings/status/close?id=${form.id}&status=${form.status}"
    }
}
