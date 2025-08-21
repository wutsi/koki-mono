package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.Size

data class UpdateListingPriceRequest(
    val price: Long? = null,
    val visitFees: Long? = null,
    @get:Size(max = 3) val currency: String? = null,
    var sellerAgentCommission: Double? = null,
    var buyerAgentCommission: Double? = null,
)
