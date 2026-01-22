package com.wutsi.koki.listing.server.service.mq

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
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
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.ImageQuality
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.listing.server.domain.ListingEntity
import com.wutsi.koki.listing.server.service.ListingService
import com.wutsi.koki.listing.server.service.ai.ListingAgentFactory
import com.wutsi.koki.listing.server.service.ai.ListingImageContentGeneratorAgent
import com.wutsi.koki.listing.server.service.ai.ListingImageContentGeneratorResult
import com.wutsi.koki.platform.logger.DefaultKVLogger
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import tools.jackson.databind.json.JsonMapper
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ListingFileUploadedEventHandlerTest {
    private val agentFactory = mock<ListingAgentFactory>()
    private val fileService = mock<FileService>()
    private val listingService = mock<ListingService>()
    private val jsonMapper = JsonMapper()
    private val logger = DefaultKVLogger()
    private val handler = ListingFileUploadedEventHandler(
        agentFactory = agentFactory,
        fileService = fileService,
        listingService = listingService,
        jsonMapper = jsonMapper,
        logger = logger,
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

    private val agent = mock<ListingImageContentGeneratorAgent>()

    @BeforeEach
    fun setUp() {
        doReturn(file).whenever(fileService).get(file.id!!, file.tenantId)
        doReturn(image).whenever(fileService).get(image.id!!, file.tenantId)
        doReturn(File("/foo/bar")).whenever(fileService).download(any())
        doReturn(totalFiles).whenever(fileService)
            .countByTypeAndOwnerIdAndOwnerType(eq(FileType.FILE), any(), any())
        doReturn(totalImages).whenever(fileService)
            .countByTypeAndOwnerIdAndOwnerType(eq(FileType.IMAGE), any(), any())
        doReturn(listing).whenever(listingService).get(any(), any())
        doReturn(agent).whenever(agentFactory).createImageContentGenerator()
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `owner not LISTING`() {
        val event = createFileEvent(ObjectType.ACCOUNT)
        handler.handle(event)

        verify(listingService, never()).save(any(), anyOrNull())
    }

    @Test
    fun onFileUploaded() {
        val event = createFileEvent()
        handler.handle(event)

        val fileArg = argumentCaptor<FileEntity>()
        verify(fileService).save(fileArg.capture())
        assertEquals(FileStatus.APPROVED, fileArg.firstValue.status)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(fileArg.firstValue.createdById))
        assertEquals(totalFiles.toInt(), listingArg.firstValue.totalFiles)
        assertEquals(listing.totalImages, listingArg.firstValue.totalImages)
    }

    @Test
    fun `onImageUploaded - Approved`() {
        val result = createImageReviewerResult(true)
        doReturn(jsonMapper.writeValueAsString(result)).whenever(agent).run(any(), any())

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
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
    }

    @Test
    fun `onImageUploaded - Approved - Listing with hero image`() {
        doReturn(listing.copy(heroImageId = 555L)).whenever(listingService).get(any(), any())

        val result = createImageReviewerResult(true)
        doReturn(jsonMapper.writeValueAsString(result)).whenever(agent).run(any(), any())

        val event = createImageEvent()
        handler.handle(event)

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(image.createdById))
        assertEquals(555L, listingArg.firstValue.heroImageId)
        assertEquals(listing.totalFiles, listingArg.firstValue.totalFiles)
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
    }

    @Test
    fun `onImageUploaded - Image not in REVIEW`() {
        doReturn(image.copy(status = FileStatus.APPROVED))
            .whenever(fileService).get(image.id!!, image.tenantId)

        val event = createImageEvent()
        handler.handle(event)

        verify(agent, never()).run(any(), any())

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(image.createdById))
        assertEquals(image.id, listingArg.firstValue.heroImageId)
        assertEquals(listing.totalFiles, listingArg.firstValue.totalFiles)
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
    }

    @Test
    fun `onImageUploaded - Rejected`() {
        val result = createImageReviewerResult(false)
        doReturn(jsonMapper.writeValueAsString(result)).whenever(agent).run(any(), any())

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

        val listingArg = argumentCaptor<ListingEntity>()
        verify(listingService).save(listingArg.capture(), eq(image.createdById))
        assertEquals(totalImages.toInt(), listingArg.firstValue.totalImages)
    }

    @Test
    fun `when file not found, rethrow the event`() {
        // GIVEN
        doThrow(NotFoundException(Error()))
            .whenever(fileService).get(any(), any())

        // WHEN
        assertThrows<NotFoundException> { handler.handle(createImageEvent()) }
    }

    private fun createFileEvent(ownerType: ObjectType = ObjectType.LISTING): FileUploadedEvent {
        return FileUploadedEvent(
            fileId = file.id!!,
            tenantId = file.tenantId,
            fileType = FileType.FILE,
            owner = ObjectReference(listing.id!!, ownerType),
        )
    }

    private fun createImageEvent(): FileUploadedEvent {
        return FileUploadedEvent(
            fileId = image.id!!,
            tenantId = image.tenantId,
            fileType = FileType.IMAGE,
            owner = ObjectReference(listing.id!!, ObjectType.LISTING),
        )
    }

    private fun createImageReviewerResult(valid: Boolean): ListingImageContentGeneratorResult {
        return ListingImageContentGeneratorResult(
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
