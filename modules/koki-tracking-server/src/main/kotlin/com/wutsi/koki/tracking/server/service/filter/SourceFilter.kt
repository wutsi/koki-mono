package com.wutsi.koki.tracking.server.service.filter

import com.wutsi.koki.platform.tracking.TrafficSourceDetector
import com.wutsi.koki.tracking.server.domain.TrackEntity
import com.wutsi.koki.tracking.server.service.Filter
import org.springframework.stereotype.Service

@Service
class SourceFilter : Filter {
    private val detector = TrafficSourceDetector()
    override fun filter(track: TrackEntity): TrackEntity {
        val source = detector.detect(track.url, track.referrer, track.ua)
        if (source == null) {
            return track
        }
        return track.copy(source = source)
    }
}
