package com.wutsi.koki.portal.offer.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.common.page.PageName
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Controller
@RequestMapping("/offers")
@RequiresPermission(["offer"])
class ListOfferController : AbstractOfferController() {
    @GetMapping
    fun list(model: Model): String {
        more(model = model)

        model.addAttribute(
            "page",
            createPageModel(
                name = PageName.OFFER,
                title = getMessage("page.offer.list.meta.title"),
            )
        )
        return "offers/list"
    }

    @GetMapping("/more")
    fun more(
        @RequestParam(name = "limit", required = false) limit: Int = 20,
        @RequestParam(name = "offset", required = false) offset: Int = 0,
        model: Model
    ): String {
        val offer = findOffers(limit, offset)
        model.addAttribute("offer", offer)

        val offers = findOffers(limit, offset)
        model.addAttribute("offers", offers)
        model.addAttribute("moreUrl", buildMoreUrl(offers, limit, offset))
        return "offers/more"
    }

    private fun buildMoreUrl(versions: List<OfferModel>, limit: Int, offset: Int): String? {
        return "/offers/more?limit=$limit&offset=" + (offset + limit)
    }

    private fun findOffers(limit: Int, offset: Int): List<OfferModel> {
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
                    expiresAt = Date(),
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
                    expiresAt = Date(),
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
                    expiresAt = null,
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
                    expiresAt = Date(),
                    expiresAtText = "2025/09/13",
                )
            ),
        )
    }
}
