package com.wutsi.koki.tracking.server.service

import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.springframework.stereotype.Service

@Service
class TrackConsumer(
    private val pipeline: Pipeline
) : Consumer {
    override fun consume(event: Any): Boolean {
       if  (event is TrackSubmittedEvent){
           pipeline.filter(toTrackEntity(event.track))
           return true
       } else {
           return false
       }
    }

    private fun toTrackEntity(track: Track): TrackEntity {
        return TrackEntity(
            time = track.time,
            correlationId = track.correlationId,
            tenantId = track.tenantId,
            accountId = track.accountId,
            productId = track.productId,
            deviceId = track.deviceId,
            page = track.page,
            component = track.component,
            url = track.url,
            ip = track.ip,
            ua = track.ua,
            lat = track.lat,
            long = track.long,
            event = track.event,
            referrer = track.referrer,
            value = track.value,
            channelType = track.channelType,
        )
    }
}
