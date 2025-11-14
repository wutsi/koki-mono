package com.wutsi.koki.lead.dto

data class SearchLeadResponse(
    val leads: List<LeadSummary> = emptyList()
)
