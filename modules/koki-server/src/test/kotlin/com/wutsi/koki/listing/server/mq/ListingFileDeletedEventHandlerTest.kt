package com.wutsi.koki.listing.server.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
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
    )

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

        doReturn(listing).whenever(listingService).get(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun onFileDeleted() {
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
    }

    @Test
    fun `onFileDeleted - no images`() {
        val event = createFileEvent(fileId = heroImageId)
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), anyOrNull())
        assertEquals(file.id, listingArg.firstValue.heroImageId)
    }

    @Test
    fun `onFileDeleted - owner not LISTING`() {
        val event = createFileEvent(ownerType = ObjectType.ACCOUNT)
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun `onFileDeleted - not hero image`() {
        val event = createFileEvent(fileId = heroImageId + 1)
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    private fun createFileEvent(
        fileId: Long = heroImageId,
        ownerType: ObjectType = ObjectType.LISTING,
    ): FileDeletedEvent {
        return FileDeletedEvent(
            fileId = fileId,
            tenantId = file.tenantId,
            owner = ObjectReference(listing.id!!, ownerType),
        )
    }
}
