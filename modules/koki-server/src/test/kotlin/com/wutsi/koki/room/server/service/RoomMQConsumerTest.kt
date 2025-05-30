package com.wutsi.koki.room.server.service

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.room.server.command.PublishRoomCommand
import com.wutsi.koki.room.server.service.event.FileUploaderHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.mock
import kotlin.test.Test

class RoomMQConsumerTest {
    private val publishRoomHandler = mock<PublishRoomCommandHandler>()
    private val fileUploadedHandler = mock<FileUploaderHandler>()

    private val consumer = RoomMQConsumer(
        publishRoomHandler = publishRoomHandler,
        fileUploadedHandler = fileUploadedHandler,
    )

    @Test
    fun `file uploaded`() {
        // GIVEN
        val event = FileUploadedEvent()
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        verify(fileUploadedHandler).handle(event)
    }

    @Test
    fun `publish room`() {
        // GIVEN
        val event = PublishRoomCommand()
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        verify(publishRoomHandler).handle(event)
    }
}
