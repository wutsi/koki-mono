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
import org.springframework.web.client.RestClientException

@Controller
@RequestMapping("/listings/publish")
@RequiresPermission(["listing:manage", "listing:full_access"])
class PublishListingController : AbstractEditListingController() {
    @GetMapping
    fun publish(@RequestParam id: Long, model: Model): String {
        val listing = findListing(id)
        model.addAttribute("listing", listing)
        model.addAttribute("form", ListingForm(id = id))

        loadPriceTrendMetrics(listing, model, listingService)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_PUBLISH,
                title = getMessage("page.listing.publish.meta.title"),
            )
        )
        return "listings/publish"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        try {
            listingService.publish(form.id)
            return "redirect:/listings/publish/done?id=${form.id}"
        } catch (ex: RestClientException) {
            loadError(ex, model)
            return publish(form.id, model)
        }
    }
}
