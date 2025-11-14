package com.wutsi.koki.lead.dto

import java.util.Date

data class UpdateLeadStatusRequest(
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val nextContactAt: Date? = null,
    val nextVisitAt: Date? = null,
)
