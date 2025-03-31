package com.wutsi.koki.file.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileMQConsumerTest {
    private val storage = mock<StorageService>()
    private val storageBuilder = mock<StorageServiceBuilder>()
    private val configurationService = mock<ConfigurationService>()
    private val extractor = mock<FileInfoExtractor>()
    private val extractorProvider = mock<FileInfoExtractorProvider>()
    private val fileService = mock<FileService>()
    private val logger = DefaultKVLogger()
    private val consumer = FileMQConsumer(
        storageBuilder = storageBuilder,
        configurationService = configurationService,
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
        language = "en"
    )

    @BeforeEach
    fun setUp() {
        doReturn(file).whenever(fileService).get(any(), any())
        doReturn(emptyList<ConfigurationEntity>()).whenever(configurationService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        doReturn(storage).whenever(storageBuilder).build(any())

        doReturn(info).whenever(extractor).extract(any())
        doReturn(extractor).whenever(extractorProvider).get(any())
    }

    @Test
    fun fileUploaded() {
        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = consumer.consume(event)

        assertTrue(result)
        assertEquals(info.numberOfPages, file.numberOfPages)
        assertEquals(info.language, file.language)
        verify(fileService).save(file)
    }

    @Test
    fun fileUploadedButNoExtractory() {
        doReturn(null).whenever(extractorProvider).get(any())

        val event = FileUploadedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = consumer.consume(event)

        assertTrue(result)
        verify(fileService, never()).save(any())
    }

    @Test
    fun fileDeleted() {
        val event = FileDeletedEvent(fileId = file.id!!, tenantId = file.tenantId)
        val result = consumer.consume(event)

        assertFalse(result)
        verify(fileService, never()).save(any())
    }
}
