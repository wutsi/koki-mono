package com.wutsi.koki.tenant.dto

data class SearchTypeResponse(
    val types: List<TypeSummary> = emptyList()
)
