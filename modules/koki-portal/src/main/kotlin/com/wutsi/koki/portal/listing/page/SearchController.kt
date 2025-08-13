package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.listing.dto.ListingType
import com.wutsi.koki.listing.dto.PropertyType
import com.wutsi.koki.portal.listing.form.ListingFilterForm
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/listings/search")
class SearchController : AbstractListingController() {
    @GetMapping
    fun search(model: Model): String {
        return "listings/search"
    }

    @GetMapping("/filter")
    fun filter(model: Model): String {
        model.addAttribute("form", ListingFilterForm())
        model.addAttribute("listingTypes", ListingType.entries)
        model.addAttribute("propertyTypes", PropertyType.entries)
        model.addAttribute("rooms", listOf("1", "1+", "2", "2+", "3", "3+", "4", "4+"))

        return "filter"
    }
}
