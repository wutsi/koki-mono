package com.wutsi.koki.offer.dto

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class OfferVersionSummary(
    val id: Long = -1,
    val offerId: Long = -1,
    val owner: ObjectReference? = null,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val price: Money = Money(),
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val submittedAt: Date = Date(),
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
)
