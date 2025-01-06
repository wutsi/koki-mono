package com.wutsi.koki.contact.dto

data class SearchContactResponse(
    val contacts: List<ContactSummary> = emptyList()
)
