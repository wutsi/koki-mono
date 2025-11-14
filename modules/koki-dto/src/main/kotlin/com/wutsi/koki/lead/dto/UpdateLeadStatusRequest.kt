package com.wutsi.koki.lead.dto

data class UpdateLeadStatusResponse(
    val status: LeadStatus = LeadStatus.UNKNOWN,
    val comment: String? = null,
)
