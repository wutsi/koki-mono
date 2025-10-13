package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers/widgets")
@RequiresPermission(["offer"])
class OfferWidgetController : AbstractOfferController() {
    @GetMapping("/active")
    fun list(@RequestParam(required = false, name = "test-mode") testMode: Boolean, model: Model): String {
        model.addAttribute("testMode", testMode)

        val offers = findOffers()
        if (offers.isNotEmpty()) {
            model.addAttribute("offers", offers)
        }
        return "offers/widget"
    }

    private fun findOffers(): List<OfferModel> {
        return offerService.search(
            statuses = listOf(OfferStatus.SUBMITTED),
            assigneeUserId = userHolder.get()?.id,
            limit = 5,
        )
    }
}
