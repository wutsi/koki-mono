package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/edit/done")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingDoneController : AbstractEditListingController() {
    @GetMapping
    fun create(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_DONE,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )
        return "listings/edit-done"
    }
}
