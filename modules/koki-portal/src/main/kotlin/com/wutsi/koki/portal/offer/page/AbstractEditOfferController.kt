package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.offer.form.OfferForm
import com.wutsi.koki.portal.offer.model.OfferModel
import org.apache.commons.lang3.time.DateUtils
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException
import java.text.SimpleDateFormat
import java.util.Date

abstract class AbstractEditOfferController : AbstractOfferDetailsController() {
    override fun findOffer(id: Long): OfferModel {
        val offer = super.findOffer(id)
        if (!offer.canManage(getUser())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }
        return offer
    }

    protected fun toOfferForm(offer: OfferModel): OfferForm {
        val df = SimpleDateFormat("yyyy-MM-dd")
        return OfferForm(
            id = offer.id,
            ownerId = offer.owner?.id,
            ownerType = offer.owner?.type,
            price = offer.version.price.amount.toLong(),
            pricePerMonth = offer.listing?.listingTypeRental == true,
            contingencies = offer.version.contingencies,
            submittingParty = offer.version.submittingParty,
            expiresAt = offer.version.expiresAt?.let { date -> df.format(date) },
            expiresAtMin = df.format(DateUtils.addDays(Date(), 1)),
        )
    }
}
