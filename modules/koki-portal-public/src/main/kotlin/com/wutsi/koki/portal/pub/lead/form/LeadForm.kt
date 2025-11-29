package com.wutsi.koki.portal.pub.agent.form

data class LeadForm(
    val listingId: Long = -1,
    val firstName: String = "",
    val lastName: String = "",
    val message: String = "",
    val email: String = "",
    val phone: String = "",
    val phoneFull: String? = "",
    val country: String? = null,
)
