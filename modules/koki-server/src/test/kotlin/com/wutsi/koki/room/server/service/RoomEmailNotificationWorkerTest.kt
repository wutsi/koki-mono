package com.wutsi.koki.room.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.message.dto.event.MessageSentEvent
import com.wutsi.koki.notification.server.service.NotificationMQConsumer
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomEmailNotificationWorkerTest {
    private val registry = mock<NotificationMQConsumer>()
    private val emailMessageSender = mock<MessageEmailSender>()
    private val worker = RoomEmailNotificationWorker(registry, emailMessageSender)

    @Test
    fun onMessageSent() {
        val event = MessageSentEvent()
        val result = worker.notify(event)

        assertEquals(true, result)
        verify(emailMessageSender).send(event)
    }

    @Test
    fun onFileUploaded() {
        val event = FileUploadedEvent()
        val result = worker.notify(event)

        assertEquals(false, result)
        verify(emailMessageSender, never()).send(any())
    }
}
