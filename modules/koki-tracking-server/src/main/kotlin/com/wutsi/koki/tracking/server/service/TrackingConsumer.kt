package com.wutsi.koki.tracking.server.service

import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.mq.Consumer
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
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
        logger.add("track_account_id", event.track.accountId)
        logger.add("track_correlation_id", event.track.correlationId)
        logger.add("track_channel_type", event.track.channelType)
        logger.add("track_component", event.track.component)
        logger.add("track_device_id", event.track.deviceId)
        logger.add("track_event", event.track.event)
        logger.add("track_ip", event.track.ip)
        logger.add("track_lat", event.track.lat)
        logger.add("track_long", event.track.long)
        logger.add("track_page", event.track.page)
        logger.add("track_product_id", event.track.productId)
        logger.add("track_product_type", event.track.productType)
        logger.add("track_recipient_id", event.track.recipientId)
        logger.add("track_referrer", event.track.referrer)
        logger.add("track_rank", event.track.rank)
        logger.add("track_tenant_id", event.track.tenantId)
        logger.add("track_ua", event.track.ua)
        logger.add("track_url", event.track.url)
        logger.add("track_value", event.track.value)

        val entities = toTrackEntity(event.track)
        entities.forEach { entity -> pipeline.filter(entity) }
    }

    private fun toTrackEntity(track: Track): List<TrackEntity> {
        val entity = TrackEntity(
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
            rank = track.rank,
            productType = track.productType,
            recipientId = track.recipientId,
        )
        if (entity.productId == null) {
            return listOf(entity)
        } else {
            val productIds = entity.productId.split("|")
            if (productIds.size <= 1) {
                return listOf(entity)
            } else {
                var index: Int = 0
                return productIds.map { productId ->
                    entity.copy(
                        productId = productId,
                        rank = if (track.event == TrackEvent.IMPRESSION) {
                            index++
                        } else {
                            track.rank
                        }
                    )
                }
            }
        }
    }
}
