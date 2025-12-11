package com.wutsi.koki.lead.dto

data class SearchLeadMessageResponse(
    val messages: List<LeadMessageSummary> = emptyList()
)
