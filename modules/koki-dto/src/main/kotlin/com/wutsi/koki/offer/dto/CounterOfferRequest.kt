package com.wutsi.koki.offer.dto

import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class CounterOfferRequest(
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val price: Money = Money(),
    val contingencies: String? = null,
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
)
