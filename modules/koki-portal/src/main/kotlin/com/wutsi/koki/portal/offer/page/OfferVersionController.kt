package com.wutsi.koki.portal.offer.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Date

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
        return OfferVersionModel(
            id = id,
            offerId = 1L,
            price = MoneyModel(amount = 450000.0, currency = "CAD", displayText = "C$ 420,000"),
            status = OfferStatus.SUBMITTED,
            contingencies = """
                        - Inspection des lieux requises avant of 30 Oct. 2025
                        - Avance de loyer de 12 mois payer d'ici le 14 Septembre
                        - Preuve de financement
                    """.trimIndent(),
            submittingParty = OfferParty.SELLER,
            createdAtText = "2025/09/12",
            expiresAt = Date(),
            expiresAtText = "2025/09/14",
        )
    }
}
