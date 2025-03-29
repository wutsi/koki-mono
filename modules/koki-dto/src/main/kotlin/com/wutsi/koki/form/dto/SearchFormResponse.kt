package com.wutsi.koki.form.dto

data class SearchFormResponse(
    val forms: List<FormSummary> = emptyList()
)
