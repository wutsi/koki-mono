package com.wutsi.koki.portal.lead

import com.wutsi.koki.lead.dto.LeadStatus

data class LeadForm(
    val id: Long = -1,
    val status: LeadStatus? = null,
    val nextVisitAt: String? = null,
    val nextContactAt: String? = null,
)
