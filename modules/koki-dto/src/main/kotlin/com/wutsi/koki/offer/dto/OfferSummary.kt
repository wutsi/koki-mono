package com.wutsi.koki.offer.dto

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.refdata.dto.Money

data class OfferSummary(
    val id: Long = -1,
    val owner: ObjectReference? = null,
    val buyerContactId: Long = -1,
    val buyerAgentUserId: Long? = null,
    val versionId: Long = -1,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val price: Money = Money(),
)
