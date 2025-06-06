package com.wutsi.koki.tracking.server.service

import com.wutsi.koki.tracking.server.domain.TrackEntity

class Pipeline(val steps: List<Filter>) : Filter {
    override fun filter(track: TrackEntity): TrackEntity {
        var cur = track
        steps.forEach {
            cur = it.filter(cur)
        }
        return cur
    }
}
