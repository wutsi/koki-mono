package com.wutsi.koki.room.server.service.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.refdata.server.domain.AmenityEntity
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.event.RoomPublishedEvent
import com.wutsi.koki.room.server.command.PublishRoomCommand
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.PublishRoomCommandHandler
import com.wutsi.koki.room.server.service.RoomMQConsumer
import com.wutsi.koki.room.server.service.RoomService
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.ai.RoomInformationAgent
import com.wutsi.koki.room.server.service.data.RoomInformationAgentData
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PublishRoomCommandHandlerTest {
    private val fileService = mock<FileService>()
    private val storageServiceProvider = mock<StorageServiceProvider>()
    private val agentFactory = mock<RoomAgentFactory>()
    private val roomService = mock<RoomService>()
    private val publisher = mock<Publisher>()
    private val objectMapper = ObjectMapper()

    private val handler = PublishRoomCommandHandler(
        fileService = fileService,
        storageServiceProvider = storageServiceProvider,
        agentFactory = agentFactory,
        roomService = roomService,
        publisher = publisher,
        objectMapper = objectMapper
    )

    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val storage = LocalStorageService(
        directory = directory, baseUrl = "http://localhost:8080/storage"
    )

    val tenantId = 111L
    private val files = listOf(
        FileEntity(
            id = 111L,
            tenantId = tenantId,
            ownerId = 222L,
            ownerType = ObjectType.ROOM,
            url = "http://localhost:8080/storage/TestFile.png",
            contentType = "image/png",
            name = "TestFile.png",
            type = FileType.IMAGE,
        ),
        FileEntity(
            id = 222L,
            tenantId = tenantId,
            ownerId = 222L,
            ownerType = ObjectType.ROOM,
            url = "http://localhost:8080/storage/TestFile.png",
            contentType = "image/png",
            name = "TestFile.png",
            type = FileType.IMAGE,
        ),
        FileEntity(
            id = 333L,
            tenantId = tenantId,
            ownerId = 222L,
            ownerType = ObjectType.ROOM,
            url = "http://localhost:8080/storage/TestFile.png",
            contentType = "image/png",
            name = "TestFile.png",
            type = FileType.IMAGE,
        ),
    )

    private val room = RoomEntity(
        id = 777L,
        tenantId = tenantId,
        numberOfBeds = -1,
        numberOfBathrooms = -1,
        numberOfRooms = -1,
        status = RoomStatus.PUBLISHING,
        amenities = mutableListOf(
            AmenityEntity(id = 500L),
            AmenityEntity(id = 501L)
        )
    )

    private val data = RoomInformationAgentData(
        title = "This is the AI title",
        description = "This is AI description",
        summary = "This is the AI summary",
        titleFr = "Titre",
        descriptionFr = "La description",
        summaryFr = "Le sommaire",
        valid = true,
        amenityIds = listOf(11L, 33L, 55L),
        numberOfBeds = 3,
        numberOfBathrooms = 5,
        numberOfBedrooms = 7,
        heroImageIndex = 1,
        heroImageReason = null,
    )

    private val agent = mock<RoomInformationAgent>()

    @BeforeEach
    fun setup() {
        doReturn(agent).whenever(agentFactory).createRoomInformationFactory(any())
        doReturn(storage).whenever(storageServiceProvider).get(any())
        doReturn(files).whenever(fileService)
            .search(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())

        doReturn(objectMapper.writeValueAsString(data)).whenever(agent).run(any(), any())

        doReturn(room).whenever(roomService).get(any(), any())
    }

    @Test
    fun publish() {
        // GIVEN
        setupFiles("/file/document.jpg", "image/jpg")

        // WHEN
        handler.handle(PublishRoomCommand(room.id!!, tenantId))

        // THEN
        val ffiles = argumentCaptor<List<File>>()
        verify(agent).run(eq(PublishRoomCommandHandler.QUERY), ffiles.capture())
        assertEquals(files.size, ffiles.firstValue.size)

        val room1 = argumentCaptor<RoomEntity>()
        verify(roomService).save(room1.capture())
        assertEquals(data.title, room1.firstValue.title)
        assertEquals(data.description, room1.firstValue.description)
        assertEquals(data.summary, room1.firstValue.summary)
        assertEquals(data.titleFr, room1.firstValue.titleFr)
        assertEquals(data.descriptionFr, room1.firstValue.descriptionFr)
        assertEquals(data.summaryFr, room1.firstValue.summaryFr)
        assertEquals(files[data.heroImageIndex].id, room1.firstValue.heroImageId)
        assertEquals(data.heroImageReason, room1.firstValue.heroImageReason)
        assertEquals(RoomStatus.PUBLISHED, room1.firstValue.status)

        verify(roomService).addAmenities(room.id, AddAmenityRequest(data.amenityIds), tenantId)

        val evt = argumentCaptor<RoomPublishedEvent>()
        verify(publisher).publish(evt.capture())
        assertEquals(room.id, evt.firstValue.roomId)
        assertEquals(room.tenantId, evt.firstValue.tenantId)
    }

    @Test
    fun `no AI agent`() {
        // GIVEN
        setupFiles("/file/document.jpg", "image/jpg")
        doReturn(null).whenever(agentFactory).createRoomInformationFactory(any())

        // WHEN
        handler.handle(PublishRoomCommand(room.id!!, tenantId))

        // THEN
        verify(agent, never()).run(any(), any())

        val room1 = argumentCaptor<RoomEntity>()
        verify(roomService).save(room1.capture())
        assertNotEquals(data.title, room1.firstValue.title)
        assertNotEquals(data.description, room1.firstValue.description)
        assertNotEquals(data.summary, room1.firstValue.summary)
        assertEquals(RoomStatus.PUBLISHED, room1.firstValue.status)

        verify(roomService, never()).addAmenities(any(), any(), any())

        val evt = argumentCaptor<RoomPublishedEvent>()
        verify(publisher).publish(evt.capture())
        assertEquals(room.id, evt.firstValue.roomId)
        assertEquals(room.tenantId, evt.firstValue.tenantId)
    }

    private fun setupFiles(path: String, contentType: String = "image/png"): File {
        val input = RoomMQConsumer::class.java.getResourceAsStream(path)
        storage.store(path = files[0].name, content = input!!, contentType, -1)

        return File("$directory/$path")
    }
}
