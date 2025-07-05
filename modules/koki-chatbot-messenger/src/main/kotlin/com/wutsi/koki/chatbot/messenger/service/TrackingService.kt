package com.wutsi.koki.chatbot.messenger.service

import com.wutsi.koki.chatbot.messenger.model.Messaging
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.stereotype.Service

@Service
class TrackingService(private val publisher: Publisher) {
    companion object {
        const val PAGE = "messenger"
    }

    fun impression(rooms: List<RoomSummary>, tenantId: Long, correlationId: String, messaging: Messaging) {
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = messaging.timestamp,
                    correlationId = correlationId,
                    accountId = null,
                    tenantId = tenantId,
                    productId = rooms.map { room -> room.id }.joinToString("|"),
                    deviceId = deviceId(messaging),
                    event = TrackEvent.IMPRESSION,
                    page = PAGE,
                    channelType = ChannelType.MESSAGING,
                )
            )
        )
    }

    fun click(
        productId: String,
        tenantId: Long,
        deviceId: String,
        correlationId: String,
        rank: Int,
        referer: String?,
        ua: String?
    ) {
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = System.currentTimeMillis(),
                    correlationId = correlationId,
                    tenantId = tenantId,
                    productId = productId,
                    deviceId = deviceId,
                    event = TrackEvent.CLICK,
                    page = PAGE,
                    channelType = ChannelType.MESSAGING,
                    rank = rank,
                    referrer = referer,
                    ua = ua,
                )
            )
        )
    }

    fun deviceId(messaging: Messaging): String {
        return "me-" + messaging.sender.id
    }
}
