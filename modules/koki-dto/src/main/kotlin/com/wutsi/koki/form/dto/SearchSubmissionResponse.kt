package com.wutsi.koki.form.dto

data class SearchSubmissionResponse(
    val formSubmissions: List<FormSubmissionSummary> = emptyList()
)
