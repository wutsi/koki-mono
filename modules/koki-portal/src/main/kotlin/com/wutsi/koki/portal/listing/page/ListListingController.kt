package com.wutsi.koki.portal.listing.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/listings")
@RequiresPermission(["listing", "listing:full_access"])
class ListListingController : AbstractListingController() {
    @GetMapping
    fun list(model: Model): String {
        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.LISTING_LIST,
                title = getMessage("page.listing.list.meta.title"),
            )
        )
        return "listings/list"
    }
}
