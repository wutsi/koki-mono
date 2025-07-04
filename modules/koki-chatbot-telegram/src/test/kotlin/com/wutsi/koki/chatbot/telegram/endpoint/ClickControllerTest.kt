package com.wutsi.koki.chatbot.telegram.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.chatbot.telegram.service.TrackingService
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.track.dto.TrackEvent
import com.wutsi.koki.track.dto.event.TrackSubmittedEvent
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ClickControllerTest {
    val publisher = mock<Publisher>()
    val trackingService = TrackingService(publisher)
    private val controller = ClickController(trackingService)

    @Test
    fun onClick() {
        val url = "http://www.google.ca"
        val result = controller.onClick(
            tenantId = 1,
            correlationId = "1111",
            productId = "123",
            deviceId = "333",
            rank = 3,
            url = url,
        )

        assertEquals("redirect:$url", result)

        val event = argumentCaptor<TrackSubmittedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(TrackEvent.CLICK, event.firstValue.track.event)
        assertEquals("telegram", event.firstValue.track.page)
        assertEquals("123", event.firstValue.track.productId)
        assertEquals("333", event.firstValue.track.deviceId)
        assertEquals(1L, event.firstValue.track.tenantId)
        assertEquals(3, event.firstValue.track.rank)
    }
}
