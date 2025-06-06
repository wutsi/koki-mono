package com.wutsi.koki.track.dto.event

import com.wutsi.koki.track.dto.Track

data class TrackSubmittedEvent(
    val track: Track = Track(),
    val timestamp: Long = System.currentTimeMillis(),
)
