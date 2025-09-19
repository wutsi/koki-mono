package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers/status/done")
@RequiresPermission(["offer:manage", "offer:full_access"])
class StatusOfferDoneController : AbstractOfferDetailsController() {
    @GetMapping
    fun done(@RequestParam id: Long, model: Model): String {
        val offer = findOffer(id)
        model.addAttribute("offer", offer)

        val status = offer.status.name.lowercase()
        model.addAttribute("title", getMessage("page.offer.status.$status.title"))
        model.addAttribute("description", getMessage("page.offer.status.$status.done"))

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER_STATUS_DONE,
                title = getMessage("page.offer.status.$status.meta.title"),
            )
        )
        return "offers/status-done"
    }
}
