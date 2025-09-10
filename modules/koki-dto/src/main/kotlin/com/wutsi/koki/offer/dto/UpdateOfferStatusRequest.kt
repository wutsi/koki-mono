package com.wutsi.koki.offer.dto

data class UpdateOfferStatusRequest(
    val status: OfferStatus = OfferStatus.UNKNOWN,
)
