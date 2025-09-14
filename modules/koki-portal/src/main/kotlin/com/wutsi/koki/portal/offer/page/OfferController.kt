package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/offers")
@RequiresPermission(["offer:manage", "offer:full_access"])
class OfferController : AbstractOfferDetailsController() {
    @GetMapping("/{id}")
    fun show(@PathVariable id: Long, model: Model): String {
        val offer = findOffer(id)
        model.addAttribute("offer", offer)

        if (offer.listing != null) {
            model.addAttribute("readOnly", offer.listing.statusOffMarket)
        }

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER,
                title = getMessage("page.offer.show.meta.title", arrayOf(offer.id)),
            )
        )
        return "offers/show"
    }
}
