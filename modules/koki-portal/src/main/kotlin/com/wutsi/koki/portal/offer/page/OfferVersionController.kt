package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/offer-versions")
@RequiresPermission(["offer"])
class OfferVersionController : AbstractOfferDetailsController() {
    @GetMapping("/{id}")
    fun list(
        @PathVariable id: Long,
        model: Model
    ): String {
        val version = findVersion(id)
        model.addAttribute("version", version)

        val offer = findOffer(version.offerId)
        model.addAttribute("offer", offer)

        return "offers/versions/show"
    }

    private fun findVersion(id: Long): OfferVersionModel {
        return offerVersionService.get(id)
    }
}
