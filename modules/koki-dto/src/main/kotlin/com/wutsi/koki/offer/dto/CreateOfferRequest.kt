package com.wutsi.koki.offer.dto

import com.wutsi.koki.common.dto.ObjectReference
import java.util.Date

data class CreateOfferRequest(
    val owner: ObjectReference? = null,
    val buyerContactId: Long = -1,
    val buyerAgentUserId: Long? = null,
    val price: Long = 0,
    val currency: String = "",
    val contingencies: String? = null,
    val expiresAt: Date? = null,
    val closingAt: Date? = null,
)
