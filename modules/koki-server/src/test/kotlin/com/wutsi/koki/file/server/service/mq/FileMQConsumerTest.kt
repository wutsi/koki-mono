package com.wutsi.koki.file.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileMQConsumerTest {
    private val fileUploadedEventHandler = mock<FileUploadedEventHandler>()
    private val createFileCommandHandler = mock<CreateFileCommandHandler>()
    private val logger: KVLogger = DefaultKVLogger()
    private val consumer = FileMQConsumer(
        fileUploadedEventHandler = fileUploadedEventHandler,
        createFileCommandHandler = createFileCommandHandler,
        logger = logger,
    )

    @AfterEach
    fun tearDown() {
        logger.log()
    }

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
    fun `fileCreated - ignore duplicate file error`() {
        // GIVEN
        val ex = ConflictException(
            error = Error(ErrorCode.FILE_ALREADY_EXISTS)
        )
        doThrow(ex).whenever(createFileCommandHandler).handle(any())

        // WHEN
        val result = consumer.consume(CreateFileCommand())

        // THEN
        assertFalse(result)
    }

    @Test
    fun `fileCreated - rethrow error`() {
        // GIVEN
        val ex = ConflictException(
            error = Error(ErrorCode.FILE_INVALID_S3_CONFIGURATION)
        )
        doThrow(ex).whenever(createFileCommandHandler).handle(any())

        // WHEN
        assertThrows<ConflictException> { consumer.consume(CreateFileCommand()) }
    }

    @Test
    fun `unsupported event`() {
        val event = emptyMap<String, String>()
        val result = consumer.consume(event)

        assertFalse(result)
        verify(fileUploadedEventHandler, never()).handle(any())
    }
}
