package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/status/done")
@RequiresPermission(["listing:manage", "listing:full_access"])
class StatusDoneListingController : AbstractListingController() {
    @GetMapping
    fun done(@RequestParam id: Long, @RequestParam status: ListingStatus, model: Model): String {
        val listing = findListing(id, status = status)
        model.addAttribute("listing", listing)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_STATUS_DONE,
                title = getMessage("page.listing.status.meta.title"),
            )
        )
        return "listings/status-done"
    }
}
