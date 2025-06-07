package com.wutsi.koki.tracking.server.service

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.springframework.stereotype.Service

@Service
class TrackingConsumer(
    private val pipeline: Pipeline,
    private val logger: KVLogger,
) : Consumer {
    override fun consume(event: Any): Boolean {
        if (event is TrackSubmittedEvent) {
            onTrackSubmitted(event)
            return true
        } else {
            return false
        }
    }

    private fun onTrackSubmitted(event: TrackSubmittedEvent) {
        logger.add("track_event", event.track.event)
        logger.add("track_product_id", event.track.productId)
        logger.add("track_account_id", event.track.accountId)
        logger.add("track_page", event.track.page)
        logger.add("track_component", event.track.component)
        logger.add("track_channel_type", event.track.channelType)

        pipeline.filter(toTrackEntity(event.track))
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
