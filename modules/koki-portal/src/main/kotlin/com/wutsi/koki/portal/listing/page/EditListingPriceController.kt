package com.wutsi.koki.portal.listing.page

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
@RequestMapping("/listings/edit/price")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingPriceController : AbstractEditorListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", toListingForm(listing))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_PRICE,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/edit-price"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        listingService.updatePrice(form)
        val listing = findListing(form.id)
        return if (listing.listingType == ListingType.RENTAL) {
            "redirect:/listings/edit/leasing?id=${form.id}"
        } else {
            "redirect:/listings/edit/seller?id=${form.id}"
        }
    }
}
