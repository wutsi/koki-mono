package com.wutsi.koki.chatbot.ai.data

data class SearchAgentData(
    val searchParameters: SearchParameters = SearchParameters(),
    val properties: List<PropertyData> = emptyList(),
)
