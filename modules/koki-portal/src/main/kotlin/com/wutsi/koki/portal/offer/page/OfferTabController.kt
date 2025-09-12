package com.wutsi.koki.portal.offer.page.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.offer.page.model.OfferModel
import com.wutsi.koki.portal.offer.page.model.OfferVersionModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/offers")
@RequiresPermission(["offer:manage", "offer:full_access"])
class OfferTabController : AbstractOfferController() {
    @GetMapping("/tab")
    fun list(
        @RequestParam(name = "owner-id", required = false) ownerId: Long? = null,
        @RequestParam(name = "owner-type", required = false) ownerType: ObjectType? = null,
        @RequestParam(name = "read-only", required = false) readOnly: Boolean = false,
        @RequestParam(name = "test-mode", required = false) testMode: Boolean = false,
        model: Model
    ): String {
        model.addAttribute("ownerId", ownerId)
        model.addAttribute("ownerType", ownerType)
        model.addAttribute("readOnly", readOnly)
        model.addAttribute("testMode", testMode)
        model.addAttribute("offers", findOffers(ownerId, ownerType))

        return "offers/tab"
    }

    private fun findOffers(ownerId: Long?, ownerType: ObjectType?): List<OfferModel> {
        return listOf(
            OfferModel(
                id = 111,
                buyerContact = ContactModel(
                    firstName = "Ray",
                    lastName = "Sponsible",
                ),
                buyerAgentUser = UserModel(
                    displayName = "Salah Vendu"
                ),
                version = OfferVersionModel(
                    id = 1110,
                    price = MoneyModel(amount = 450000.0, currency = "CAD", displayText = "C$ 450,000"),
                    status = OfferStatus.ACCEPTED,
                    contingencies = """
                        - Inspection des lieux requises avant of 30 Oct. 2025
                        - Avance de loyer de 12 mois payer d'ici le 14 Septembre
                        - Preuve de financement
                    """.trimIndent(),
                    submittingParty = OfferParty.BUYER,
                    createdAtText = "2025/09/12",
                    expiresAtText = "2025/09/14",
                )
            ),

            OfferModel(
                id = 222,
                buyerContact = ContactModel(
                    firstName = "Roger",
                    lastName = "Milla",
                ),
                buyerAgentUser = UserModel(
                    displayName = "Jane Doe"
                ),
                version = OfferVersionModel(
                    id = 2220,
                    price = MoneyModel(amount = 460000.0, currency = "CAD", displayText = "C$ 460,000"),
                    status = OfferStatus.SUBMITTED,
                    contingencies = """
                        - Inspection des lieux requises avant of 30 Oct. 2025
                        - Avance de loyer de 12 mois payer d'ici le 14 Septembre
                        - Preuve de financement
                    """.trimIndent(),
                    submittingParty = OfferParty.SELLER,
                    createdAtText = "2025/09/11",
                    expiresAtText = "2025/09/14",
                )
            ),

            OfferModel(
                id = 333,
                buyerContact = ContactModel(
                    firstName = "Emanuel",
                    lastName = "Kunde",
                ),
                buyerAgentUser = UserModel(
                    displayName = "Paul Atangana"
                ),
                version = OfferVersionModel(
                    id = 3330,
                    price = MoneyModel(amount = 430000.0, currency = "CAD", displayText = "C$ 430,000"),
                    status = OfferStatus.REJECTED,
                    submittingParty = OfferParty.SELLER,
                    createdAtText = "2025/10/11",
                    expiresAtText = null,
                )
            ),

            OfferModel(
                id = 444,
                buyerContact = ContactModel(
                    firstName = "Omam",
                    lastName = "Mbiyick",
                ),
                buyerAgentUser = UserModel(
                    displayName = "Paul Atangana"
                ),
                version = OfferVersionModel(
                    id = 4440,
                    price = MoneyModel(amount = 400000.0, currency = "CAD", displayText = "C$ 400,000"),
                    status = OfferStatus.EXPIRED,
                    submittingParty = OfferParty.BUYER,
                    createdAtText = "2025/09/09",
                    expiresAtText = "2025/09/13",
                )
            ),
        )
    }
}
