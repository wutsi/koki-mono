package com.wutsi.koki.room.server.service

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
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.domain.LabelEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.LabelService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.room.server.domain.RoomEntity
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.ai.RoomImageAgent
import com.wutsi.koki.room.server.service.data.RoomImageAgentData
import com.wutsi.koki.room.server.service.event.FileUploaderHandler
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FileUploaderHandlerTest {
    private val fileService = mock<FileService>()
    private val labelService = mock<LabelService>()
    private val storageServiceProvider = mock<StorageServiceProvider>()
    private val agentFactory = mock<RoomAgentFactory>()
    private val roomService = mock<RoomService>()
    private val objectMapper = ObjectMapper()

    private val handler = FileUploaderHandler(
        fileService = fileService,
        labelService = labelService,
        storageServiceProvider = storageServiceProvider,
        agentFactory = agentFactory,
        objectMapper = objectMapper,
        roomService = roomService,
    )

    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val storage = LocalStorageService(
        directory = directory, baseUrl = "http://localhost:8080/storage"
    )

    val tenantId = 111L
    private val file = FileEntity(
        id = 111L,
        tenantId = tenantId,
        ownerId = 222L,
        ownerType = ObjectType.ROOM,
        url = "http://localhost:8080/storage/TestFile.png",
        contentType = "image/png",
        name = "TestFile.png",
        type = FileType.IMAGE,
    )

    val data = RoomImageAgentData(
        title = "This is the title",
        description = "The description",
        valid = true,
        hashtags = listOf("#A", "#B", "#C"),
        quality = 2,
    )

    val labels = listOf(
        LabelEntity(displayName = "X"),
        LabelEntity(displayName = "Y"),
        LabelEntity(displayName = "Z"),
    )

    val room = RoomEntity(
        id = 111L,
        heroImageId = null,
    )

    private val agent = mock<RoomImageAgent>()

    @BeforeEach
    fun setup() {
        doReturn(agent).whenever(agentFactory).createRoomImageAgent(any())
        doReturn(storage).whenever(storageServiceProvider).get(any())
        doReturn(file).whenever(fileService).get(any(), any())
        doReturn(room).whenever(roomService).get(any(), any())

        doReturn(objectMapper.writeValueAsString(data)).whenever(agent).run(any(), anyOrNull())

        doReturn(labels).whenever(labelService).findOrCreate(any(), any())
    }

    @Test
    fun `image uploaded`() {
        // GIVEN
        setupFile("/file/document.jpg", "image/jpg")

        // WHEN
        val event = FileUploadedEvent(
            fileId = file.id!!,
            owner = ObjectReference(id = file.ownerId!!, type = ObjectType.ROOM),
        )
        handler.handle(event)

        // THEN
        verify(agent).run(eq(FileUploaderHandler.IMAGE_AGENT_QUERY), any())

        val file1 = argumentCaptor<FileEntity>()
        verify(fileService).save(file1.capture())
        assertEquals(file.id, file1.firstValue.id)
        assertEquals(data.title, file1.firstValue.title)
        assertEquals(data.description, file1.firstValue.description)
        assertEquals(FileStatus.APPROVED, file1.firstValue.status)
        assertEquals(null, file1.firstValue.rejectionReason)
        assertEquals(labels, file1.firstValue.labels)

        val room1 = argumentCaptor<RoomEntity>()
        verify(roomService).save(room1.capture())
        assertEquals(file.id, room1.firstValue.heroImageId)
    }

    @Test
    fun `image uploaded - image not valid`() {
        // GIVEN
        setupFile("/file/document.jpg", "image/jpg")
        doReturn(
            objectMapper.writeValueAsString(
                data.copy(
                    valid = false,
                    reason = "Sexual content!!"
                )
            )
        ).whenever(agent).run(any(), anyOrNull())

        // WHEN
        val event = FileUploadedEvent(
            fileId = file.id!!,
            owner = ObjectReference(id = file.ownerId!!, type = ObjectType.ROOM),
        )
        handler.handle(event)

        // THEN
        verify(agent).run(eq(FileUploaderHandler.IMAGE_AGENT_QUERY), any())

        val file1 = argumentCaptor<FileEntity>()
        verify(fileService).save(file1.capture())
        assertEquals(file.id, file1.firstValue.id)
        assertEquals(data.title, file1.firstValue.title)
        assertEquals(data.description, file1.firstValue.description)
        assertEquals(FileStatus.REJECTED, file1.firstValue.status)
        assertEquals("Sexual content!!", file1.firstValue.rejectionReason)
        assertEquals(labels, file1.firstValue.labels)

        val room1 = argumentCaptor<RoomEntity>()
        verify(roomService).save(room1.capture())
        assertEquals(null, room1.firstValue.heroImageId)
    }

    @Test
    fun `image uploaded - room with heroImage`() {
        // GIVEN
        setupFile("/file/document.jpg", "image/jpg")
        doReturn(room.copy(heroImageId = 777L)).whenever(roomService).get(any(), any())

        // WHEN
        val event = FileUploadedEvent(
            fileId = file.id!!,
            owner = ObjectReference(id = file.ownerId!!, type = ObjectType.ROOM),
        )
        handler.handle(event)

        // THEN
        verify(fileService).save(any())
        verify(roomService, never()).save(any())
    }

    @Test
    fun `file uploaded`() {
        // GIVEN
        doReturn(file.copy(type = FileType.FILE)).whenever(fileService).get(any(), any())

        // GIVEN
        val event = FileUploadedEvent(
            fileId = file.id!!
        )
        handler.handle(event)

        // THEN
        verify(agent, never()).run(any(), anyOrNull())
        verify(fileService, never()).save(any())
    }

    @Test
    fun `no AI agent`() {
        // GIVEN
        doReturn(null).whenever(agentFactory).createRoomImageAgent(any())

        // GIVEN
        val event = FileUploadedEvent(fileId = file.id!!)
        handler.handle(event)

        // THEN
        verify(agent, never()).run(any(), anyOrNull())
        verify(fileService, never()).save(any())
    }

    @Test
    fun `non-room image`() {
        // GIVEN
        doReturn(file.copy(ownerType = ObjectType.UNKNOWN)).whenever(fileService).get(any(), any())

        // WHEN
        val event = FileUploadedEvent(fileId = file.id!!)
        handler.handle(event)

        // THEN
        verify(agent, never()).run(any(), anyOrNull())
        verify(fileService, never()).save(any())
    }

    private fun setupFile(path: String, contentType: String = "image/png"): File {
        val input = RoomMQConsumer::class.java.getResourceAsStream(path)
        storage.store(path = file.name, content = input!!, contentType, -1)

        return File("$directory/$path")
    }
}
