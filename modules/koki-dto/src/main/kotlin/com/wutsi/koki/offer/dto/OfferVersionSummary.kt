package com.wutsi.koki.offer.dto

import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class OfferVersionSummary(
    val id: Long = -1,
    val offerId: Long = -1,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val assigneeUserId: Long? = null,
    val price: Money = Money(),
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val createdAt: Date = Date(),
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
    val modifiedAt: Date = Date(),
)
