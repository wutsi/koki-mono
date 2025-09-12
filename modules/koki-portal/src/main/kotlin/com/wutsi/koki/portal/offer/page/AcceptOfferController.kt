package com.wutsi.koki.portal.offer.page

import com.wutsi.blog.portal.common.model.MoneyModel
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.offer.dto.OfferParty
import com.wutsi.koki.offer.dto.OfferStatus
import com.wutsi.koki.portal.common.model.ObjectReferenceModel
import com.wutsi.koki.portal.contact.model.ContactModel
import com.wutsi.koki.portal.listing.model.ListingModel
import com.wutsi.koki.portal.module.page.AbstractModuleDetailsPageController
import com.wutsi.koki.portal.offer.model.OfferModel
import com.wutsi.koki.portal.offer.model.OfferVersionModel
import com.wutsi.koki.portal.refdata.model.AddressModel
import com.wutsi.koki.portal.refdata.model.LocationModel
import com.wutsi.koki.portal.user.model.UserModel
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException
import java.util.Date

abstract class AbstractOfferDetailsController : AbstractModuleDetailsPageController() {
    companion object {
        const val MODULE_NAME = "offer"
    }

    override fun getModuleName(): String {
        return MODULE_NAME
    }

    protected open fun findOffer(id: Long): OfferModel {
        val offer = OfferModel(
            id = id,
            owner = ObjectReferenceModel(
                type = ObjectType.LISTING,
                id = 1L,
                title = "K100123",
            ),
            buyerContact = ContactModel(
                firstName = "Ray",
                lastName = "Sponsible",
            ),
            buyerAgentUser = UserModel(
                id = 1L,
                displayName = "Salah Vendu",
                photoUrl = "https://picsum.photos/800/600",
                employer = "REIMAX Auteuil",
                mobileText = "(514) 758-0199",
                mobile = "+15147580199",
            ),
            sellerAgentUser = UserModel(
                id = 22L,
                displayName = "Pele",
                photoUrl = "https://picsum.photos/500/500",
                employer = "IMMO Spot",
                mobileText = "(514) 777-1199",
                mobile = "+15147771199",
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
                createdAt = Date(),
                createdAtText = "2025/09/12",
                expiresAt = Date(),
                expiresAtText = "2025/09/14",
                closingAtText = "2025/12/14",
            ),
            listing = ListingModel(
                id = 340493,
                listingNumber = 3409403,
                heroImageUrl = "https://picsum.photos/600/600",
                address = AddressModel(
                    country = "CA",
                    countryName = "Canada",
                    street = "3030 Linton",
                    neighbourhood = LocationModel(name = "Auteuil"),
                    city = LocationModel(name = "Laval"),
                    postalCode = "H7K 1C6"
                ),
                bedrooms = 3,
                bathrooms = 3,
                propertyArea = 750,
            ),
        )

        if (!offer.canAccess(getUser())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }
        return offer
    }
}
