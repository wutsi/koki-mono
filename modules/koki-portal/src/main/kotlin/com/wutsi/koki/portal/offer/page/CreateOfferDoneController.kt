package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers/create/done")
@RequiresPermission(["offer:manage", "offer:full_access"])
class CreateOfferDoneController : AbstractOfferDetailsController() {
    @GetMapping
    fun done(@RequestParam id: Long, model: Model): String {
        val offer = findOffer(id)
        model.addAttribute("offer", offer)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_CREATE_DONE,
                title = getMessage("page.offer.create.meta.title"),
            )
        )
        return "offers/create-done"
    }
}
