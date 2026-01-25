package com.wutsi.koki.portal.pub.tracking.service

import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.tracing.DeviceIdProvider
import com.wutsi.koki.platform.tracking.ChannelTypeProvider
import com.wutsi.koki.portal.pub.tenant.service.CurrentTenantHolder
import com.wutsi.koki.portal.pub.tracking.form.TrackForm
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class TrackService(
    private val publisher: Publisher,
    private val deviceIdProvider: DeviceIdProvider,
    private val channelTypeProvider: ChannelTypeProvider,
    private val tenant: CurrentTenantHolder,
    private val request: HttpServletRequest,
) {
    fun track(form: TrackForm) {
        val channelType = channelTypeProvider.get(request)
        publisher.publish(
            TrackSubmittedEvent(
                timestamp = System.currentTimeMillis(),
                track = Track(
                    time = form.time,
                    correlationId = form.hitId,
                    accountId = null,
                    tenantId = tenant.get().id,
                    productId = form.productId,
                    deviceId = deviceIdProvider.get(request),
                    event = form.event,
                    url = form.url,
                    ua = form.ua,
                    value = form.value,
                    page = form.page,
                    component = form.component,
                    referrer = form.referrer,
                    ip = remoteIp(),
                    channelType = channelType,
                    rank = form.rank,
                    productType = form.productType,
                )
            )
        )
    }

    fun remoteIp(): String {
        val ip = request.getHeader("X-FORWARDED-FOR")
        return if (ip.isNullOrEmpty()) {
            request.remoteAddr
        } else {
            ip
        }
    }
}
