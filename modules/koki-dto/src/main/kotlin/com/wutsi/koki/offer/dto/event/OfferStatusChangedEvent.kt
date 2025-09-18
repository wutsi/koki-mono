package com.wutsi.koki.offer.dto.event

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.offer.dto.OfferStatus

data class OfferStatusChangedEvent(
    val offerId: Long = -1,
    val tenantId: Long = -1,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val owner: ObjectReference? = null,
    val timestamp: Long = System.currentTimeMillis()
)
