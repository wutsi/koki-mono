package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.BasementType
import com.wutsi.koki.listing.dto.FenceType
import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.ParkingType
import com.wutsi.koki.listing.dto.PropertyType
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
@RequestMapping("/listings/edit")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingController : AbstractEditListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", toListingForm(listing))
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )
        model.addAttribute("listingTypes", ListingType.entries)
        model.addAttribute("propertyTypes", PropertyType.entries)
        model.addAttribute("basementTypes", BasementType.entries)
        model.addAttribute("parkingTypes", ParkingType.entries)
        model.addAttribute("fenceTypes", FenceType.entries)
        return "listings/edit"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        listingService.update(form)
        return "redirect:/listings/edit/amenities?id=${form.id}"
    }
}
