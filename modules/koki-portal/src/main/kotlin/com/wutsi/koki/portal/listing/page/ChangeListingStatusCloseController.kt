package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.listing.form.ListingForm
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.RestClientException

@Controller
@RequestMapping("/listings/status/close")
@RequiresPermission(["listing:manage", "listing:full_access"])
class ChangeListingStatusCloseController : AbstractEditListingController() {
    @GetMapping
    fun close(@RequestParam id: Long, @RequestParam status: ListingStatus, model: Model): String {
        val listing = findListing(id)
        val form = toListingForm(listing).copy(status = status)
        return close(form, model, listing)
    }

    private fun close(form: ListingForm, model: Model, listing: ListingModel?): String {
        model.addAttribute("listing", listing ?: findListing(form.id))
        model.addAttribute("closing", form.status == ListingStatus.SOLD || form.status == ListingStatus.RENTED)
        model.addAttribute("form", form)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_STATUS_CLOSE,
                title = getMessage("page.listing.status.meta.title"),
            )
        )
        return "listings/status-close"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        try {
            listingService.close(form)
            return "redirect:/listings/status/done?id=${form.id}"
        } catch (ex: RestClientException) {
            loadError(ex, model)
            return close(form, model, null)
        }
    }
}
