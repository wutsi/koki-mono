package com.wutsi.koki.portal.listing.form

data class AIListingForm(
    val text: String = "",
    val cityId: Long? = null,
    val country: String = "",
    val neighbourhoodId: Long? = null,
)
