package com.wutsi.koki.portal.listing.form

data class AIListingForm(
    val text: String = "",
    val cityId: Long = -1,
    val neighbourhoodId: Long? = null,
)
