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
class EditListingAddressController : AbstractListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val city = resolveCity()
        val parent = resolveParent(city)
        model.addAttribute("city", city)
        model.addAttribute("cityName",
            city?.let {
                parent?.let { "${city.name}, ${parent.name}" } ?: city.name
            }
        )

        loadCountries(model)
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
                country = city?.country,
                cityId = city?.id,
            )
        )
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_ADDRESS,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/address"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/edit/remarks?id=${form.id}"
    }
}
