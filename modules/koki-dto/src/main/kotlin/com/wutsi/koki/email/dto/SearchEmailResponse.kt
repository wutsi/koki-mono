package com.wutsi.koki.email.dto

data class SearchEmailResponse(
    val emails: List<EmailSummary> = emptyList()
)
