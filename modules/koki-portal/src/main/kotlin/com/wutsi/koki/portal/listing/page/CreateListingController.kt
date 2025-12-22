package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingType
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

@Controller
@RequestMapping("/listings/create")
@RequiresPermission(["listing:manage", "listing:full_access"])
class CreateListingController : AbstractListingController() {
    @GetMapping
    fun create(model: Model): String {
        model.addAttribute("form", ListingForm())
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_CREATE,
                title = getMessage("page.listing.create.meta.title"),
            )
        )
        model.addAttribute("listingTypes", ListingType.entries)
        model.addAttribute("propertyTypes", PropertyType.entries)
        return "listings/create"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm): String {
        val id = listingService.create(form)
        return "redirect:/listings/$id"
    }
}
