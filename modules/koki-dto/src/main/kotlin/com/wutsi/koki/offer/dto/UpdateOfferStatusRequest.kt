package com.wutsi.koki.offer.dto

import java.util.Date

data class UpdateOfferStatusRequest(
    val status: OfferStatus = OfferStatus.UNKNOWN,
    val comment: String? = null,
    val closedAt: Date? = null
)
