package com.wutsi.koki.chatbot

import com.wutsi.koki.chatbot.ai.data.SearchParameters
import com.wutsi.koki.refdata.dto.Location
import com.wutsi.koki.room.dto.RoomSummary

data class ChatbotResponse(
    val rooms: List<RoomSummary> = emptyList(),
    val searchLocation: Location? = null,
    val searchParameters: SearchParameters? = null
)
