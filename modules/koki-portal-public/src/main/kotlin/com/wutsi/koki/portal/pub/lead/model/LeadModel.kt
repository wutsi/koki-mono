package com.wutsi.koki.portal.pub.lead.model

import com.wutsi.koki.lead.dto.LeadSource
import com.wutsi.koki.lead.dto.LeadStatus

data class LeadModel(
    val id: Long = -1,
    val listingId: Long? = null,
    val userId: Long? = null,
    val source: LeadSource = LeadSource.UNKNOWN,
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val firstName: String = "",
    val lastName: String = "",
    val email: String? = null,
    val phoneNumber: String = "",
)
