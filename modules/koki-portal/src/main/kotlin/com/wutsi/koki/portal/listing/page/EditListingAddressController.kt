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
@RequestMapping("/listings/edit/address")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingAddressController : AbstractEditListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val defaultCity = resolveCity()
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", toListingForm(listing, defaultCity))

        val city = listing.address?.city ?: defaultCity
        if (city != null) {
            val parent = resolveParent(city)
            model.addAttribute("city", city)
            model.addAttribute("cityName", parent?.let { "${city.name}, ${parent.name}" } ?: city.name)

            val neighbourhood = listing.address?.neighbourhood
            if (neighbourhood != null) {
                model.addAttribute("neighbourhood", neighbourhood)
                model.addAttribute("neighbourhoodName", "${neighbourhood.name}, ${city.name}")
            }
        }

        loadCountries(model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_ADDRESS,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/edit-address"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        listingService.updateAddress(form)
        return "redirect:/listings/edit/geo-location?id=${form.id}"
    }
}
