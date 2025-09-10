package com.wutsi.koki.offer.dto

import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.refdata.dto.Money
import java.util.Date

data class Offer(
    val id: Long = -1,
    val owner: ObjectReference? = null,
    val buyerContactId: Long = -1,
    val versionId: Long = -1,
    val buyerAgentUserId: Long? = null,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val price: Money = Money(),
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
