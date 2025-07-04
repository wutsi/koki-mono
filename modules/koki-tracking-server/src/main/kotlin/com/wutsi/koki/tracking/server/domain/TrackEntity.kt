package com.wutsi.koki.tracking.server.domain

import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.DeviceType
import com.wutsi.koki.track.dto.TrackEvent

data class TrackEntity(
    val time: Long = 0,
    val correlationId: String? = null,
    val deviceId: String? = null,
    val accountId: String? = null,
    val tenantId: Long? = null,
    val productId: String? = null,
    val ua: String? = null,
    val bot: Boolean = false,
    val ip: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val referrer: String? = null,
    val page: String? = null,
    val component: String? = null,
    val event: TrackEvent = TrackEvent.UNKNOWN,
    val value: String? = null,
    val url: String? = null,
    val source: String? = null,
    val campaign: String? = null,
    val channelType: ChannelType = ChannelType.UNKNOWN,
    val deviceType: DeviceType = DeviceType.UNKNOWN,
    val country: String? = null,
    val rank: Int? = null,
)
