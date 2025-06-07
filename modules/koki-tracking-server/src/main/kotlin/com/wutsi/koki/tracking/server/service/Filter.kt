package com.wutsi.koki.tracking.server.service

import com.wutsi.koki.tracking.server.domain.TrackEntity

interface Filter {
    fun filter(track: TrackEntity): TrackEntity
}
