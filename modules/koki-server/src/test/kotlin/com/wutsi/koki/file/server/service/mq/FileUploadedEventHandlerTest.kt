package com.wutsi.koki.file.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import com.wutsi.koki.file.server.service.FileInfoExtractorProvider
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.tenant.server.service.StorageProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileUploadedEventHandlerTest {
    private val storage = mock<StorageService>()
    private val storageProvider = mock<StorageProvider>()
    private val extractor = mock<FileInfoExtractor>()
    private val extractorProvider = mock<FileInfoExtractorProvider>()
    private val fileService = mock<FileService>()
    private val logger = DefaultKVLogger()
    private val handler = FileUploadedEventHandler(
        storageProvider = storageProvider,
        extractorProvider = extractorProvider,
        fileService = fileService,
        logger = logger,
    )

    private val file = FileEntity(
        id = 1111L,
        tenantId = 333L,
        contentType = "application/pdf",
        url = "https://www.cof/com.pdf",
        type = FileType.FILE
    )

    private val image = FileEntity(
        id = 1111L,
        tenantId = 333L,
        contentType = "image/png",
        url = "https://www.cof/com.png",
        type = FileType.IMAGE
    )

    private val info = FileInfo(
        numberOfPages = 430,
        language = "en",
        width = 1000,
        height = 600,
    )

    @BeforeEach
    fun setUp() {
        doReturn(file).whenever(fileService).get(any(), any())

        doReturn(storage).whenever(storageProvider).get(any())

        doReturn(info).whenever(extractor).extract(any())
        doReturn(extractor).whenever(extractorProvider).get(any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertTrue(result)
        assertEquals(info.numberOfPages, file.numberOfPages)
        assertEquals(info.language, file.language)
        assertEquals(null, file.width)
        assertEquals(null, file.height)
        verify(fileService).save(file)
        verify(extractor).extract(any())
    }

    @Test
    fun `fileUploaded - numberOfPages NULL AND language NOT NULL`() {
        doReturn(file.copy(numberOfPages = 333))
            .whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertTrue(result)
        verify(fileService).save(any())
        verify(extractor).extract(any())
    }

    @Test
    fun `fileUploaded - numberOfPages NOT NULL AND language NULL`() {
        doReturn(file.copy(language = "fr"))
            .whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertTrue(result)
        verify(fileService).save(any())
        verify(extractor).extract(any())
    }

    @Test
    fun `fileUploaded - numberOfPages NOT NULL AND language NOT NULL`() {
        doReturn(file.copy(numberOfPages = 333, language = "fr"))
            .whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertFalse(result)
        verify(fileService, never()).save(any())
        verify(extractor, never()).extract(any())
    }

    @Test
    fun imageUploaded() {
        doReturn(image).whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertTrue(result)
        assertEquals(null, image.numberOfPages)
        assertEquals(null, image.language)
        assertEquals(info.width, image.width)
        assertEquals(info.height, image.height)
        verify(fileService).save(image)
        verify(extractor).extract(any())
    }

    @Test
    fun `imageUploaded - width NULL AND heigh NOT NULL`() {
        doReturn(image.copy(height = 111)).whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertTrue(result)
        verify(fileService).save(any())
        verify(extractor).extract(any())
    }

    @Test
    fun `imageUploaded - width NOT NULL AND heigh NULL`() {
        doReturn(image.copy(width = 111)).whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = handler.handle(event)

        assertTrue(result)
        verify(fileService).save(any())
        verify(extractor).extract(any())
    }

    @Test
    fun `fileUploaded - image has width AND height THEN not processed`() {
        doReturn(image.copy(width = 100, height = 100)).whenever(fileService).get(any(), any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        handler.handle(event)

        verify(extractor, never()).extract(any())
        verify(fileService, never()).save(any())
    }

    @Test
    fun `no extractor`() {
        doReturn(null).whenever(extractorProvider).get(any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        handler.handle(event)

        verify(fileService, never()).save(any())
    }

    @Test
    fun error() {
        doThrow(IOException::class).whenever(extractor).extract(any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        handler.handle(event)

        verify(fileService, never()).save(any())
    }
}
