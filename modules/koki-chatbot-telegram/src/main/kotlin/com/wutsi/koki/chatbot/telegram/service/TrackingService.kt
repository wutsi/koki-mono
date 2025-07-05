package com.wutsi.koki.chatbot.telegram.service

import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.room.dto.RoomSummary
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class TrackingService(private val publisher: Publisher) {
    companion object {
        const val PAGE = "telegram"
    }

    fun impression(rooms: List<RoomSummary>, tenantId: Long, correlationId: String, update: Update) {
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = System.currentTimeMillis(),
                    correlationId = correlationId,
                    accountId = null,
                    tenantId = tenantId,
                    productId = rooms.map { room -> room.id }.joinToString("|"),
                    deviceId = deviceId(update),
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
        ua: String?,
        referer: String?
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
                    ua = ua,
                    referrer = referer,
                )
            )
        )
    }

    fun deviceId(update: Update): String {
        return "tg-" + update.message.from.id.toString()
    }
}
