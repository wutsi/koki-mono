package com.wutsi.koki.file.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileMQConsumerTest {
    private val fileUploadedEventHandler = mock<FileUploadedEventHandler>()
    private val consumer = FileMQConsumer(
        fileUploadedEventHandler = fileUploadedEventHandler
    )

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent(fileId = 1L, tenantId = 11L)
        val result = consumer.consume(event)

        assertTrue(result)
        verify(fileUploadedEventHandler).handle(event)
    }

    @Test
    fun fileDeleted() {
        val event = FileDeletedEvent(fileId = 1L, tenantId = 11L)
        val result = consumer.consume(event)

        assertFalse(result)
        verify(fileUploadedEventHandler, never()).handle(any())
    }
}
