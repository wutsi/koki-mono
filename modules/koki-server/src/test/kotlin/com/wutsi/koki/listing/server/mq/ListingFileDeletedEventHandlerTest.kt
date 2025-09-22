package com.wutsi.koki.listing.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.logger.KVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test

class ListingFileDeletedEventHandlerTest {
    private val fileService: FileService = mock<FileService>()
    private val listingService: ListingService = mock<ListingService>()
    private val logger: KVLogger = DefaultKVLogger()
    private val handler = ListingFileDeletedEventHandler(
        fileService = fileService,
        listingService = listingService,
        logger = logger,
    )

    private val tenantId = 1L
    private val heroImageId = 555L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
        heroImageId = heroImageId,
        totalFiles = null,
        totalImages = null,
    )

    private val totalImages = 11L
    private val totalFiles = 7L
    private val file = FileEntity(
        id = 100L,
        tenantId = tenantId,
        status = FileStatus.APPROVED,
        type = FileType.FILE,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        url = "https://picsum.photos/200/300",
        createdById = 777L,
    )

    @BeforeEach
    fun setUp() {
        doReturn(listOf(file)).whenever(fileService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        doReturn(totalImages).whenever(fileService)
            .countByTypeAndOwnerIdAndOwnerType(eq(FileType.IMAGE), any(), any())
        doReturn(totalFiles).whenever(fileService)
            .countByTypeAndOwnerIdAndOwnerType(eq(FileType.FILE), any(), any())

        doReturn(listing).whenever(listingService).get(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun onImageDeleted() {
        doReturn(emptyList<FileEntity>()).whenever(fileService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
        val event = createFileEvent(fileId = heroImageId)
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), anyOrNull())
        assertEquals(null, listingArg.firstValue.heroImageId)
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
        assertEquals(null, listingArg.firstValue.totalFiles)
    }

    @Test
    fun `onImageDeleted - no images`() {
        val event = createFileEvent(fileId = heroImageId)
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), anyOrNull())
        assertEquals(listing.heroImageId, listingArg.firstValue.heroImageId)
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
        assertEquals(null, listingArg.firstValue.totalFiles)
    }

    @Test
    fun `onImageDeleted - owner not LISTING`() {
        val event = createFileEvent(ownerType = ObjectType.ACCOUNT)
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun `onImageDeleted - not hero image`() {
        val event = createFileEvent(fileId = heroImageId + 1)
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), anyOrNull())
        assertEquals(listing.heroImageId, listingArg.firstValue.heroImageId)
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
        assertEquals(null, listingArg.firstValue.totalFiles)
    }

    @Test
    fun onFileDeleted() {
        val event = createFileEvent(fileId = file.id!!, fileType = FileType.FILE)
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), anyOrNull())
        assertEquals(listing.heroImageId, listingArg.firstValue.heroImageId)
        assertEquals(null, listingArg.firstValue.totalImages)
        assertEquals(totalFiles.toInt(), listingArg.firstValue.totalFiles)
    }

    private fun createFileEvent(
        fileId: Long = heroImageId,
        ownerType: ObjectType = ObjectType.LISTING,
        fileType: FileType = FileType.IMAGE,
    ): FileDeletedEvent {
        return FileDeletedEvent(
            fileId = fileId,
            fileType = fileType,
            tenantId = file.tenantId,
            owner = ObjectReference(listing.id!!, ownerType),
        )
    }
}
