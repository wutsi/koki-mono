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
@RequestMapping("/listings/edit/remarks")
@RequiresPermission(["listing:manage", "listing:full_access"])
class EditListingRemarksController : AbstractListingController() {
    @GetMapping
    fun edit(@RequestParam id: Long, model: Model): String {
        model.addAttribute(
            "form",
            ListingForm(
                id = id,
            )
        )
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_EDIT_REMARK,
                title = getMessage("page.listing.edit.meta.title"),
            )
        )

        return "listings/edit-remarks"
    }

    @PostMapping
    fun submit(@ModelAttribute form: ListingForm, model: Model): String {
        return "redirect:/listings/${form.id}"
    }
}
