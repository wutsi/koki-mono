package com.wutsi.koki.portal.pub.lead.form

data class LeadForm(
    val listingId: Long = -1,
    val firstName: String = "",
    val lastName: String = "",
    val message: String = "",
    val email: String = "",
    val phone: String = "",
    val phoneFull: String = "",
    val country: String? = null,
    val publicUrl: String? = null,
)
