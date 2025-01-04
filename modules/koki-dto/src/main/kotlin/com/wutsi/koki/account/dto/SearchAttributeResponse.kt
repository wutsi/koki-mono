package com.wutsi.koki.account.dto

data class SearchAttributeResponse(
    val attributes: List<AttributeSummary> = emptyList()
)
