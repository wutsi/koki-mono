package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.refdata.dto.IDType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/listings/edit/seller")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingSellerController : AbstractEditListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", toListingForm(listing))

        loadCountries(model)
        model.addAttribute("idTypes", IDType.entries)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_SELLER,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/edit-seller"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        listingService.updateSeller(form)
        return "redirect:/listings/edit/done?id=${form.id}"
    }
}
