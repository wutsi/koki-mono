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
@RequestMapping("/listings/edit/contract")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingContractController : AbstractListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        val city = resolveCity()
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
                country = city?.country,
            )
        )

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_CONTRACT,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/contract"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/edit/remarks?id=${form.id}"
    }
}
