package com.wutsi.koki.portal.offer.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.security.RequiresPermission
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Controller
@RequestMapping("/offer-versions")
@RequiresPermission(["offer"])
class ListOfferVersionController : AbstractOfferDetailsController() {
    @GetMapping
    fun list(
        @RequestParam(name = "offerId") offerId: Long,
        model: Model
    ): String {
        val offer = findOffer(offerId)
        model.addAttribute("offer", offer)

        val versions = findVersions(offerId)
        model.addAttribute("versions", versions)
        return "offers/versions/list"
    }

    private fun findVersions(offerId: Long): List<OfferVersionModel> {
        return listOf(
            OfferVersionModel(
                id = 1110,
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
            ),
            OfferVersionModel(
                id = 1110,
                price = MoneyModel(amount = 450000.0, currency = "CAD", displayText = "C$ 430,000"),
                status = OfferStatus.REJECTED,
                contingencies = """
                        - Inspection des lieux requises avant of 30 Oct. 2025
                        - Avance de loyer de 12 mois payer d'ici le 14 Septembre
                        - Preuve de financement
                    """.trimIndent(),
                submittingParty = OfferParty.BUYER,
                createdAtText = "2025/09/12",
                expiresAt = Date(),
                expiresAtText = "2025/09/14",
            ),
            OfferVersionModel(
                id = 1110,
                price = MoneyModel(amount = 450000.0, currency = "CAD", displayText = "C$ 400,000"),
                status = OfferStatus.REJECTED,
                contingencies = """
                        - Inspection des lieux requises avant of 30 Oct. 2025
                        - Avance de loyer de 12 mois payer d'ici le 14 Septembre
                        - Preuve de financement
                    """.trimIndent(),
                submittingParty = OfferParty.SELLER,
                createdAtText = "2025/09/12",
                expiresAt = Date(),
                expiresAtText = "2025/09/14",
            ),
            OfferVersionModel(
                id = 1110,
                price = MoneyModel(amount = 450000.0, currency = "CAD", displayText = "C$ 450,000"),
                status = OfferStatus.REJECTED,
                contingencies = """
                        - Inspection des lieux requises avant of 30 Oct. 2025
                        - Avance de loyer de 12 mois payer d'ici le 14 Septembre
                        - Preuve de financement
                    """.trimIndent(),
                submittingParty = OfferParty.BUYER,
                createdAtText = "2025/09/12",
                expiresAt = Date(),
                expiresAtText = "2025/09/14",
            ),
        )
    }
}
