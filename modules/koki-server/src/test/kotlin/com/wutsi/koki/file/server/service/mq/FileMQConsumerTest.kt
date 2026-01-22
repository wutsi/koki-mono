package com.wutsi.koki.file.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileMQConsumerTest {
    private val fileUploadedEventHandler = mock<FileUploadedEventHandler>()
    private val createFileCommandHandler = mock<CreateFileCommandHandler>()
    private val consumer = FileMQConsumer(
        fileUploadedEventHandler = fileUploadedEventHandler,
        createFileCommandHandler = createFileCommandHandler,
    )

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent(fileId = 1L, tenantId = 11L)
        val result = consumer.consume(event)

        assertTrue(result)
        verify(fileUploadedEventHandler).handle(event)
    }

    @Test
    fun fileCreated() {
        val event = CreateFileCommand(
            url = "https://example.com/file.png",
            tenantId = 11L,
            owner = ObjectReference(
                type = ObjectType.LISTING,
                id = 123L
            )
        )
        val result = consumer.consume(event)

        assertTrue(result)
        verify(createFileCommandHandler).handle(event)
    }

    @Test
    fun `unsupported event`() {
        val event = emptyMap<String, String>()
        val result = consumer.consume(event)

        assertFalse(result)
        verify(fileUploadedEventHandler, never()).handle(any())
    }
}
