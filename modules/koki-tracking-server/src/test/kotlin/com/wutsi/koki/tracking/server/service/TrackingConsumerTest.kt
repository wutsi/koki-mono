package com.wutsi.koki.tracking.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.track.dto.ChannelType
import com.wutsi.koki.track.dto.DeviceType
import com.wutsi.koki.track.dto.Track
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import com.wutsi.koki.tracking.server.domain.TrackEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrackingConsumerTest {
    private val pipeline = mock<Pipeline>()
    private val logger = DefaultKVLogger()
    private val consumer = TrackingConsumer(pipeline, logger)

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `tracking event submitted`() {
        val track = createTrack()
        consumer.consume(TrackSubmittedEvent(track = track))

        val entity = argumentCaptor<TrackEntity>()
        verify(pipeline).filter(entity.capture())

        assertEquals(track.time, entity.firstValue.time)
        assertEquals(track.ua, entity.firstValue.ua)
        assertEquals(track.correlationId, entity.firstValue.correlationId)
        assertEquals(track.event, entity.firstValue.event)
        assertEquals(track.productId, entity.firstValue.productId)
        assertEquals(track.page, entity.firstValue.page)
        assertEquals(track.value, entity.firstValue.value)
        assertEquals(track.tenantId, entity.firstValue.tenantId)
        assertEquals(track.lat, entity.firstValue.lat)
        assertEquals(track.long, entity.firstValue.long)
        assertEquals(track.ip, entity.firstValue.ip)
        assertEquals(track.deviceId, entity.firstValue.deviceId)
        assertEquals(track.accountId, entity.firstValue.accountId)
        assertEquals(track.referrer, entity.firstValue.referrer)
        assertEquals(track.url, entity.firstValue.url)
        assertEquals(track.channelType, entity.firstValue.channelType)
        assertEquals(null, entity.firstValue.source)
        assertEquals(null, entity.firstValue.campaign)
        assertEquals(DeviceType.UNKNOWN, entity.firstValue.deviceType)
    }

    @Test
    fun `other event`() {
        consumer.consume(FileUploadedEvent())

        verify(pipeline, never()).filter(any())
    }

    private fun createTrack() = Track(
        time = System.currentTimeMillis(),
        ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows Phone OS 7.5; Trident/5.0; IEMobile/9.0)",
        correlationId = "123",
        event = TrackEvent.VIEW,
        productId = "1234",
        page = "SR",
        value = "yo",
        tenantId = 1L,
        long = 111.0,
        lat = 222.0,
        ip = "1.1.2.3",
        deviceId = "sample-device",
        accountId = "333",
        referrer = "https://www.google.ca",
        url = "https://www.wutsi.com/read/123/this-is-nice?utm_source=email&utm_campaign=test&utm_medium=email",
        channelType = ChannelType.WEB,
    )
}
