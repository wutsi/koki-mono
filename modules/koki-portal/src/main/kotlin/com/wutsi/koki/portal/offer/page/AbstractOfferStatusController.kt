package com.wutsi.koki.portal.offer.page

import com.wutsi.koki.portal.offer.model.OfferModel
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.HttpClientErrorException

abstract class AbstractOfferStatusController : AbstractEditOfferController() {
    fun findSubmittedOffer(id: Long): OfferModel {
        val offer = super.findOffer(id)
        if (!offer.statusSubmitted || !offer.canAcceptOrRejectOrCounter(getUser())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }
        return offer
    }

    fun findAcceptedRejectedOffer(id: Long): OfferModel {
        val offer = super.findOffer(id)
        if (!(offer.statusRejected || offer.statusRejected) && offer.canCloseOrCancel(getUser())) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403))
        }
        return offer
    }
}
