package com.wutsi.koki.listing.dto

data class UpdateListingRemarksRequest(
    val publicRemarks: String? = null,
    val agentRemarks: String? = null,
)
