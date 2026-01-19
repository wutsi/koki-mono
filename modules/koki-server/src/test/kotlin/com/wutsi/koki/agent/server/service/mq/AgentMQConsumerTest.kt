package com.wutsi.koki.agent.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.tenant.dto.event.UserCreatedEvent
import org.mockito.Mockito.mock
import kotlin.test.Test

class AgentMQConsumerTest {
    private val userCreatedEventHandler = mock<AgentUserCreatedEventHandler>()

    private val consumer = AgentMQConsumer(
        userCreatedEventHandler = userCreatedEventHandler,
    )

    @Test
    fun onUserCreatedEvent() {
        val event = UserCreatedEvent()
        consumer.consume(event)

        verify(userCreatedEventHandler).handle(event)
    }

    @Test
    fun notSupported() {
        val event = FileUploadedEvent()
        consumer.consume(event)

        verify(userCreatedEventHandler, never()).handle(any())
    }
}
