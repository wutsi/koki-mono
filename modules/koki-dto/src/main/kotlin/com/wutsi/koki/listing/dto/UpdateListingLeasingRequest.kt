package com.wutsi.koki.listing.dto

data class UpdateListingLeasingRequest(
    val leaseTerm: Int? = null,
    val noticePeriod: Int? = null,
    val advanceRent: Int? = null,
    val securityDeposit: Int? = null,
)
