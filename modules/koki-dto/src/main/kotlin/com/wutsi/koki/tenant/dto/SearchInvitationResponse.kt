package com.wutsi.koki.tenant.dto

data class SearchInvitationResponse(
    val invitations: List<InvitationSummary> = emptyList()
)
