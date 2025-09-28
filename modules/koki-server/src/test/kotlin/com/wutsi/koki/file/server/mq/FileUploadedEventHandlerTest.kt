package com.wutsi.koki.file.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileInfo
import com.wutsi.koki.file.server.service.FileInfoExtractor
import com.wutsi.koki.file.server.service.FileInfoExtractorProvider
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageProvider
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.StorageService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

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
    fun handle() {
        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        handler.handle(event)

        assertEquals(info.numberOfPages, file.numberOfPages)
        assertEquals(info.language, file.language)
        assertEquals(info.width, file.width)
        assertEquals(info.height, file.height)
        verify(fileService).save(file)
    }

    @Test
    fun fileUploadedButNoExtraction() {
        doReturn(null).whenever(extractorProvider).get(any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        handler.handle(event)

        verify(fileService, never()).save(any())
    }
}
