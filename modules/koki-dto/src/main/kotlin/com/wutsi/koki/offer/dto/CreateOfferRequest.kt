package com.wutsi.koki.offer.dto

import com.wutsi.koki.common.dto.ObjectReference
import jakarta.validation.constraints.Size
import java.util.Date

data class CreateOfferRequest(
    val owner: ObjectReference? = null,
    val buyerContactId: Long = -1,
    val buyerAgentUserId: Long = -1,
    val sellerAgentUserId: Long = -1,
    val submittingParty: OfferParty = OfferParty.UNKNOWN,
    val price: Long = 0,
    @get:Size(min = 3, max = 3) val currency: String = "",
    val contingencies: String? = null,
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
)
