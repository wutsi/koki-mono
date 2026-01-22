package com.wutsi.koki.file.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.command.CreateFileCommand
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.mq.Publisher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock

class CreateFileCommandHandlerTest {
    private val fileService = mock<FileService>()
    private val publisher = mock<Publisher>()
    private val logger = DefaultKVLogger()
    private val handler = CreateFileCommandHandler(
        fileService = fileService,
        publisher = publisher,
        logger = logger
    )

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun handle() {
        // GIVEN
        val file = FileEntity(id = 333L, tenantId = 123L, type = FileType.IMAGE)
        doReturn(file).whenever(fileService).create(any(), any())

        // WHEN
        val cmd = CreateFileCommand(
            url = "https://example.com/image.png",
            tenantId = file.tenantId,
            owner = ObjectReference(type = ObjectType.LISTING, id = 456L)
        )
        handler.handle(cmd)

        // THEN
        val request = argumentCaptor<CreateFileRequest>()
        verify(fileService).create(request.capture(), eq(cmd.tenantId))
        assertEquals(cmd.url, request.firstValue.url)
        assertEquals(cmd.owner, request.firstValue.owner)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.type, event.firstValue.fileType)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(cmd.owner, event.firstValue.owner)
    }

    @Test
    fun `on error when creating files, do not publish event`() {
        // GIVEN
        val ex = ConflictException(
            error = Error(ErrorCode.FILE_ALREADY_EXISTS)
        )
        doThrow(ex).whenever(fileService).create(any(), any())

        // WHEN
        val cmd = CreateFileCommand(
            url = "https://example.com/image.png",
            tenantId = 1111,
            owner = ObjectReference(type = ObjectType.LISTING, id = 456L)
        )
        assertThrows<ConflictException> { handler.handle(cmd) }

        // THEN
        verify(publisher, never()).publish(any())
    }
}
