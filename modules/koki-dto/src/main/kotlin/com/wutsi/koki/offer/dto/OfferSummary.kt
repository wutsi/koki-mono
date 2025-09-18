package com.wutsi.koki.offer.dto

import com.wutsi.koki.common.dto.ObjectReference
import java.util.Date

data class OfferSummary(
    val id: Long = -1,
    val offerId: Long = -1,
    val owner: ObjectReference? = null,
    val versionId: Long = -1,
    val sellerAgentUserId: Long = -1,
    val buyerAgentUserId: Long = -1,
    val buyerContactId: Long = -1,
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val totalVersions: Int = 0,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
)
