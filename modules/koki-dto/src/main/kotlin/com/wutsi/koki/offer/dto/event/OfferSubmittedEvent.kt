package com.wutsi.koki.offer.dto.event

import com.wutsi.koki.common.dto.ObjectReference

data class OfferSubmittedEvent(
    val offerId: Long = -1,
    val versionId: Long = -1,
    val tenantId: Long = -1,
    val owner: ObjectReference? = null,
    val timestamp: Long = System.currentTimeMillis()
)
