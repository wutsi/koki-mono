package com.wutsi.koki.portal.offer.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.security.RequiresPermission
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Date

@Controller
@RequestMapping("/offers/widget")
@RequiresPermission(["offer"])
class OfferWidgetController : AbstractOfferController() {
    @GetMapping
    fun list(model: Model): String {
        val offers = findOffers()
        model.addAttribute("offers", offers)
        return "offers/widget"
    }

    private fun findOffers(): List<OfferModel> {
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
                ),
                listing = ListingModel(
                    listingNumber = 35000950,
                    heroImageUrl = "https://picsum.photos/800/600",
                    address = AddressModel(
                        city = LocationModel(name = "Laval"),
                        neighbourhood = LocationModel(name = "Auteuil"),
                        country = "CA",
                        street = "340 Pascal",
                    ),
                ),
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
                ),
                listing = ListingModel(
                    listingNumber = 35009111,
                    heroImageUrl = "https://picsum.photos/800/600",
                    address = AddressModel(
                        city = LocationModel(name = "Laval"),
                        neighbourhood = LocationModel(name = "Auteuil"),
                        country = "CA",
                        street = "340 Pascal",
                    ),
                ),
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
                ),
                listing = ListingModel(
                    listingNumber = 35000949,
                    heroImageUrl = "https://picsum.photos/800/600",
                    address = AddressModel(
                        city = LocationModel(name = "Laval"),
                        neighbourhood = LocationModel(name = "Auteuil"),
                        country = "CA",
                        street = "340 Pascal",
                    ),
                ),
            ),
        )
    }
}
