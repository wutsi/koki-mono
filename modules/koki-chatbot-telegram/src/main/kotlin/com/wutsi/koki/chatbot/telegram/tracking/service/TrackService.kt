package com.wutsi.koki.chatbot.telegram.tracking.service

import com.wutsi.koki.chatbot.telegram.form.TrackForm
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TrackService(
    private val publisher: Publisher,
    private val tenantProvider: TenantProvider,
) {
    fun track(form: TrackForm) {
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = form.time,
                    correlationId = UUID.randomUUID().toString(),
                    accountId = null,
                    tenantId = tenantProvider.id(),
                    productId = form.productId,
                    deviceId = form.deviceId,
                    event = form.event,
                    value = form.value,
                    page = "telegram",
                    channelType = ChannelType.MESSAGING,
                )
            )
        )
    }
}
