package com.wutsi.koki.message.dto

data class SearchMessageResponse(
    val messages: List<MessageSummary> = emptyList()
)
