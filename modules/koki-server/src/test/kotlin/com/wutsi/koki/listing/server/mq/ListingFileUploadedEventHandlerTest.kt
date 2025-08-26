package com.wutsi.koki.listing.server.mq

import com.fasterxml.jackson.databind.ObjectMapper
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
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageProvider
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.room.server.server.agent.ImageReviewerAgent
import com.wutsi.koki.room.server.server.agent.ImageReviewerAgentResult
import com.wutsi.koki.room.server.server.agent.ListingAgentFactory
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingFileUploadedEventHandlerTest {
    private val agentFactory = mock<ListingAgentFactory>()
    private val fileService = mock<FileService>()
    private val listingService = mock<ListingService>()
    private val storageProvider = mock<StorageProvider>()
    private val objectMapper = ObjectMapper()
    private val handler = ListingFileUploadedEventHandler(
        agentFactory = agentFactory,
        fileService = fileService,
        listingService = listingService,
        storageProvider = storageProvider,
        objectMapper = objectMapper
    )

    private val tenantId = 1L
    private val totalFiles = 9L
    private val totalImages = 11L
    private val listing = ListingEntity(
        id = 333L,
        tenantId = tenantId,
        heroImageId = null,
        totalImages = null,
        totalFiles = null
    )

    private val file = FileEntity(
        id = 100L,
        tenantId = tenantId,
        status = FileStatus.UNDER_REVIEW,
        type = FileType.FILE,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        url = "https://picsum.photos/200/300",
        createdById = 777L,
    )
    private val image = FileEntity(
        id = 111L,
        tenantId = tenantId,
        status = FileStatus.UNDER_REVIEW,
        type = FileType.IMAGE,
        ownerId = listing.id,
        ownerType = ObjectType.LISTING,
        url = "https://picsum.photos/200/300",
        createdById = 777L,
    )

    private val agent = mock<ImageReviewerAgent>()
    private val storage = mock<StorageService>()

    @BeforeEach
    fun setUp() {
        doReturn(file).whenever(fileService).get(file.id!!, file.tenantId)
        doReturn(image).whenever(fileService).get(image.id!!, file.tenantId)
        doReturn(totalFiles).whenever(fileService).countByTypeAndOwnerIdAndOwnerType(eq(FileType.FILE), any(), any())
        doReturn(totalImages).whenever(fileService).countByTypeAndOwnerIdAndOwnerType(eq(FileType.IMAGE), any(), any())
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(agent).whenever(agentFactory).createImageReviewerAgent(any())
        doReturn(storage).whenever(storageProvider).get(any())
    }

    @Test
    fun `file not uploaded to a listing`() {
        val event = createFileEvent(ObjectType.ACCOUNT)
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun `file uploaded`() {
        val event = createFileEvent()
        handler.handle(event)

        val fileArg = argumentCaptor<FileEntity>()
        verify(fileService).save(fileArg.capture())
        assertEquals(FileStatus.APPROVED, fileArg.firstValue.status)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(fileArg.firstValue.createdById))
        assertEquals(totalFiles, listingArg.firstValue.totalFiles)
        assertEquals(listing.totalImages, listingArg.firstValue.totalImages)
    }

    @Test
    fun `image uploaded - Approved`() {
        val result = createImageReviewerResult(true)
        doReturn(objectMapper.writeValueAsString(result)).whenever(agent).run(any(), any())

        val event = createImageEvent()
        handler.handle(event)

        val imageArg = argumentCaptor<FileEntity>()
        verify(fileService).save(imageArg.capture())
        assertEquals(FileStatus.APPROVED, imageArg.firstValue.status)
        assertEquals(result.title, imageArg.firstValue.title)
        assertEquals(result.titleFr, imageArg.firstValue.titleFr)
        assertEquals(result.description, imageArg.firstValue.description)
        assertEquals(result.descriptionFr, imageArg.firstValue.descriptionFr)
        assertEquals(result.quality, imageArg.firstValue.imageQuality)
        assertEquals(null, imageArg.firstValue.rejectionReason)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(imageArg.firstValue.createdById))
        assertEquals(imageArg.firstValue.id, listingArg.firstValue.heroImageId)
        assertEquals(listing.totalFiles, listingArg.firstValue.totalFiles)
        assertEquals(totalImages, listingArg.firstValue.totalImages)
    }

    @Test
    fun `image uploaded - Already Approved`() {
        doReturn(image.copy(status = FileStatus.APPROVED)).whenever(fileService).get(image.id!!, image.tenantId)

        val event = createImageEvent()
        handler.handle(event)

        verify(agent, never()).run(any(), any())

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(image.createdById))
        assertEquals(image.id, listingArg.firstValue.heroImageId)
        assertEquals(listing.totalFiles, listingArg.firstValue.totalFiles)
        assertEquals(totalImages, listingArg.firstValue.totalImages)
    }

    @Test
    fun `image uploaded - Approved - listing with hero image`() {
        doReturn(listing.copy(heroImageId = 555L)).whenever(listingService).get(any(), any())

        val result = createImageReviewerResult(true)
        doReturn(objectMapper.writeValueAsString(result)).whenever(agent).run(any(), any())

        val event = createImageEvent()
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(image.createdById))
        assertEquals(555L, listingArg.firstValue.heroImageId)
        assertEquals(listing.totalFiles, listingArg.firstValue.totalFiles)
        assertEquals(totalImages, listingArg.firstValue.totalImages)
    }

    @Test
    fun `image uploaded - Rejected`() {
        val result = createImageReviewerResult(false)
        doReturn(objectMapper.writeValueAsString(result)).whenever(agent).run(any(), any())

        val event = createImageEvent()
        handler.handle(event)

        val imageArg = argumentCaptor<FileEntity>()
        verify(fileService).save(imageArg.capture())
        assertEquals(FileStatus.REJECTED, imageArg.firstValue.status)
        assertEquals(result.title, imageArg.firstValue.title)
        assertEquals(result.titleFr, imageArg.firstValue.titleFr)
        assertEquals(result.description, imageArg.firstValue.description)
        assertEquals(result.descriptionFr, imageArg.firstValue.descriptionFr)
        assertEquals(result.quality, imageArg.firstValue.imageQuality)
        assertEquals(result.reason, imageArg.firstValue.rejectionReason)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    private fun createFileEvent(ownerType: ObjectType = ObjectType.LISTING): FileUploadedEvent {
        return FileUploadedEvent(
            fileId = file.id!!,
            tenantId = file.tenantId,
            owner = ObjectReference(listing.id!!, ownerType),
        )
    }

    private fun createImageEvent(): FileUploadedEvent {
        return FileUploadedEvent(
            fileId = image.id!!,
            tenantId = image.tenantId,
            owner = ObjectReference(listing.id!!, ObjectType.LISTING),
        )
    }

    private fun createImageReviewerResult(valid: Boolean): ImageReviewerAgentResult {
        return ImageReviewerAgentResult(
            title = "Bonjour",
            titleFr = "Hello",
            description = "Bonjour le monde",
            descriptionFr = "Hello world",
            valid = valid,
            reason = "C'est bon",
            quality = ImageQuality.HIGH,
        )
    }
}
