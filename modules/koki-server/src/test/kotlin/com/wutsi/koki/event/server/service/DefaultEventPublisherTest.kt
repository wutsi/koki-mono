package com.wutsi.koki.event.server.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.springframework.context.ApplicationEventPublisher
import kotlin.test.Test

class DefaultEventPublisherTest {
    val delegate = mock<ApplicationEventPublisher>()
    val publisher = DefaultEventPublisher(delegate)

    @Test
    fun publish() {
        publisher.publish("A")
        verify(delegate).publishEvent("A")
    }
}
