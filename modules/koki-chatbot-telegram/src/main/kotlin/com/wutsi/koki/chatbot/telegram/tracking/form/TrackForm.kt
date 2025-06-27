package com.wutsi.koki.chatbot.telegram.form

import com.wutsi.koki.track.dto.TrackEvent

data class TrackForm(
    val time: Long = -1,
    val productId: String? = null,
    val event: TrackEvent = TrackEvent.UNKNOWN,
    val value: String? = null,
    val deviceId: String? = null,
)
