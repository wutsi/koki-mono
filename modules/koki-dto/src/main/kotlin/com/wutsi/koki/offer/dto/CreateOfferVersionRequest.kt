package com.wutsi.koki.offer.dto

import jakarta.validation.constraints.Size
import java.util.Date

data class CreateOfferVersionRequest(
    val offerId: Long = -1,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val price: Long = 0,
    @get:Size(min = 3, max = 3) val currency: String = "",
    val contingencies: String? = null,
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
)
