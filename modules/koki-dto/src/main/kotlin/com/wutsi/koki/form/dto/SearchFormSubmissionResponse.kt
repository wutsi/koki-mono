package com.wutsi.koki.form.dto

data class SearchFormSubmissionResponse(
    val formSubmissions: List<FormSubmissionSummary> = emptyList()
)
