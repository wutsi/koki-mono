package com.wutsi.koki.contact.dto

data class SearchContactTypeResponse(
    val contactTypes: List<ContactTypeSummary> = emptyList()
)
