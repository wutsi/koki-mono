package com.wutsi.koki.track.dto

data class Track(
    val time: Long = 0,
    val correlationId: String? = null,
    val deviceId: String? = null,
    val accountId: String? = null,
    val tenantId: Long? = null,
    val productId: String? = null,
    val ua: String? = null,
    val ip: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val referrer: String? = null,
    val page: String? = null,
    val component: String? = null,
    val event: TrackEvent = TrackEvent.UNKNOWN,
    val value: String? = null,
    val url: String? = null,
    val channelType: ChannelType = ChannelType.UNKNOWN,
)
