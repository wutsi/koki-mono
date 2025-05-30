package com.wutsi.koki.room.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.room.dto.AddAmenityRequest
import com.wutsi.koki.room.dto.FurnishedType
import com.wutsi.koki.room.dto.RoomStatus
import com.wutsi.koki.room.dto.event.RoomPublishedEvent
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.RoomPublisher
import com.wutsi.koki.room.server.service.RoomService
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.ai.RoomInformationAgent
import com.wutsi.koki.room.server.service.data.RoomInformationAgentData
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomPublisherTest {
    private val fileService = mock<FileService>()
    private val storageServiceProvider = mock<StorageServiceProvider>()
    private val roomService = mock<RoomService>()
    private val agentFactory = mock<RoomAgentFactory>()
    private val publisher = mock<Publisher>()
    private val objectMapper = ObjectMapper()

    private val roomPublisher = RoomPublisher(
        fileService = fileService,
        storageServiceProvider = storageServiceProvider,
        roomService = roomService,
        agentFactory = agentFactory,
        publisher = publisher,
        objectMapper = objectMapper,
    )

    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val storage = LocalStorageService(
        directory = directory, baseUrl = "http://localhost:8080/storage"
    )

    private val roomId = 111L
    private val tenantId = 333L
    private val room = RoomEntity(
        id = roomId,
        tenantId = tenantId,
        status = RoomStatus.PUBLISHING,
        furnishedType = FurnishedType.FULLY_FURNISHED,
    )

    val images = listOf(
        FileEntity(
            id = 100,
            name = "T1.png",
            title = "Living room",
            contentType = "image/png",
            contentLength = 800L * 600,
            url = "http://localhost:8080/storage/T1.png",
        ),
        FileEntity(
            id = 101,
            name = "T2.png",
            title = null,
            contentType = "image/png",
            contentLength = 600L * 600,
            url = "http://localhost:8080/storage/T2.png",
        ),
        FileEntity(
            id = 102,
            name = "T3.png",
            title = null,
            contentType = "image/png",
            contentLength = 600L * 600,
            url = "http://localhost:8080/storage/T3.png",
        ),
    )

    private val agent = mock<RoomInformationAgent>()
    private val data = RoomInformationAgentData(
        title = "This is the title",
        summary = "This is the summary of the property",
        description = "This is the long description of the property",
        amenityIds = listOf(1L, 2L, 3L),
        numberOfBeds = 1,
        numberOfBedrooms = 3,
        numberOfBathrooms = 7,
        heroImageIndex = 2,
        heroImageReason = "Best image",
        valid = true
    )

    @BeforeEach
    fun setUp() {
        doReturn(room.copy()).whenever(roomService).get(roomId, tenantId)

        doReturn(images).whenever(fileService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        doReturn(agent).whenever(agentFactory).createRoomInformationFactory(any())
        doReturn(objectMapper.writeValueAsString(data)).whenever(agent).run(any(), any())

        doReturn(storage).whenever(storageServiceProvider).get(any())

        images.forEach { image -> setupFiles(image, "image/jpg") }
    }

    @Test
    fun publish() {
        roomPublisher.publish(roomId, tenantId)

        val files = argumentCaptor<List<File>>()
        verify(agent).run(eq(RoomPublisher.INFORMATION_AGENT_QUERY), files.capture())
        assertEquals(images.size, files.firstValue.size)

        val rm = argumentCaptor<RoomEntity>()
        verify(roomService).save(rm.capture())
        assertEquals(data.title, rm.firstValue.title)
        assertEquals(data.description, rm.firstValue.description)
        assertEquals(images[data.heroImageIndex].id, rm.firstValue.heroImageId)
        assertEquals(data.heroImageReason, rm.firstValue.heroImageReason)
        assertEquals(RoomStatus.PUBLISHED, rm.firstValue.status)

        val request = argumentCaptor<AddAmenityRequest>()
        verify(roomService).addAmenities(eq(roomId), request.capture(), eq(tenantId))
        assertEquals(data.amenityIds, request.firstValue.amenityIds)

        val event = argumentCaptor<RoomPublishedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(roomId, event.firstValue.roomId)
        assertEquals(tenantId, event.firstValue.tenantId)
    }

    @Test
    fun `no ai agent`() {
        doReturn(null).whenever(agentFactory).createRoomInformationFactory(any())

        roomPublisher.publish(roomId, tenantId)

        verify(agent, never()).run(any(), any())

        val rm = argumentCaptor<RoomEntity>()
        verify(roomService).save(rm.capture())
        assertEquals(room.title, rm.firstValue.title)
        assertEquals(room.description, rm.firstValue.description)
        assertEquals(room.heroImageId, rm.firstValue.heroImageId)
        assertEquals(room.heroImageReason, rm.firstValue.heroImageReason)
        assertEquals(RoomStatus.PUBLISHED, rm.firstValue.status)

        verify(roomService, never()).addAmenities(any(), any(), any())

        val event = argumentCaptor<RoomPublishedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(roomId, event.firstValue.roomId)
        assertEquals(tenantId, event.firstValue.tenantId)
    }

    @Test
    fun `already published`() {
        doReturn(room.copy(status = RoomStatus.PUBLISHED)).whenever(roomService).get(roomId, tenantId)

        roomPublisher.publish(roomId, tenantId)

        verify(agent, never()).run(any(), any())
        verify(roomService, never()).save(any())
        verify(roomService, never()).addAmenities(any(), any(), any())
        verify(publisher, never()).publish(any())
    }

    private fun setupFiles(image: FileEntity, contentType: String = "image/png"): File {
        val path = "/file/document.jpg"
        val input = RoomPublisherTest::class.java.getResourceAsStream(path)
        storage.store(path = image.name, content = input!!, contentType, -1)

        return File("$directory/$path")
    }
}
