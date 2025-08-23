package com.wutsi.koki.portal.listing.page

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
@RequestMapping("/listings/edit/geo-location")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingGeoLocationController : AbstractEditListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", toListingForm(listing))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_GEOLOCATION,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/edit-geo-location"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        listingService.updateGeoLocation(form)
        return "redirect:/listings/edit/remarks?id=${form.id}"
    }
}
