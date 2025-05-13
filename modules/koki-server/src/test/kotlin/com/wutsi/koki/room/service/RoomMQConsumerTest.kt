package com.wutsi.koki.room.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
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
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.room.server.service.RoomMQConsumer
import com.wutsi.koki.room.server.service.ai.RoomAgentFactory
import com.wutsi.koki.room.server.service.ai.RoomImageAgent
import com.wutsi.koki.room.server.service.data.RoomImageAgentData
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomMQConsumerTest {
    private val fileService = mock<FileService>()
    private val configurationService = mock<ConfigurationService>()
    private val labelService = mock<LabelService>()
    private val storageServiceProvider = mock<StorageServiceProvider>()
    private val agentFactory = mock<RoomAgentFactory>()
    private val logger = DefaultKVLogger()
    private val objectMapper = ObjectMapper()

    private val consumer = RoomMQConsumer(
        fileService = fileService,
        configurationService = configurationService,
        labelService = labelService,
        storageServiceProvider = storageServiceProvider,
        agentFactory = agentFactory,
        logger = logger,
        objectMapper = objectMapper
    )

    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val storage = LocalStorageService(
        directory = directory,
        baseUrl = "http://localhost:8080/storage"
    )

    val tenantId = 111L
    private val file = FileEntity(
        id = 111L,
        tenantId = tenantId,
        ownerId = 222L,
        ownerType = ObjectType.ROOM,
        url = "http://localhost:8080/storage/TestFile.png",
        contentType = "images/png",
        name = "TestFile.png",
        type = FileType.IMAGE,
    )

    private val configs = mapOf(
        ConfigurationName.AI_PROVIDER to "GEMINI",
        ConfigurationName.TAX_AI_AGENT_ENABLED to "1"
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

    private val roomImageAgent = mock<RoomImageAgent>()

    @BeforeEach
    fun setup() {
        doReturn(roomImageAgent).whenever(agentFactory).createRoomImageAgent(any())
        doReturn(storage).whenever(storageServiceProvider).get(any())
        doReturn(file).whenever(fileService).get(any(), any())
        doReturn(
            configs.map { entry -> ConfigurationEntity(name = entry.key, value = entry.value) }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

        doReturn(objectMapper.writeValueAsString(data)).whenever(roomImageAgent).run(any(), anyOrNull())

        doReturn(labels).whenever(labelService).findOrCreate(any(), any())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun imageUploaded() {
        // GIVEN

        // GIVEN
        val event = FileUploadedEvent(
            fileId = file.id!!,
            owner = ObjectReference(id = file.ownerId!!, type = ObjectType.ROOM),
        )
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        val ff = argumentCaptor<FileEntity>()
        verify(fileService).save(ff.capture())
        assertEquals(file.id, ff.firstValue.id)
        assertEquals(data.title, ff.firstValue.title)
        assertEquals(data.description, ff.firstValue.description)
        assertEquals(FileStatus.APPROVED, ff.firstValue.status)
        assertEquals(null, ff.firstValue.rejectionReason)
        assertEquals(labels, ff.firstValue.labels)
    }
}
