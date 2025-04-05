package com.wutsi.koki.tax.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.ai.server.service.AIMQConsumer
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.platform.ai.llm.LLM
import com.wutsi.koki.platform.ai.llm.LLMRequest
import com.wutsi.koki.platform.ai.llm.LLMResponse
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.Message
import com.wutsi.koki.platform.ai.llm.Role
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageServiceBuilder
import com.wutsi.koki.platform.storage.StorageType
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.springframework.http.MediaType
import java.io.ByteArrayInputStream
import java.io.OutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TaxAIAgentTest {
    private val registry = mock<AIMQConsumer>()
    private val llmProvider = mock<LLMProvider>()
    private val fileService = mock<FileService>()
    private val storageBuilder = mock<StorageServiceBuilder>()
    private val configurationService = mock<ConfigurationService>()
    private val objectMapper = ObjectMapper()
    private val logger = DefaultKVLogger()
    private val agent = TaxAIAgent(
        fileService = fileService,
        storageBuilder = storageBuilder,
        llmProvider = llmProvider,
        configurationService = configurationService,
        objectMapper = objectMapper,
        registry = registry,
        logger = logger,
    )

    private val configs = mapOf(
        ConfigurationName.AI_PROVIDER to LLMType.GEMINI,

        ConfigurationName.TAX_AI_AGENT_ENABLED to "1",

        ConfigurationName.STORAGE_TYPE to StorageType.S3,
    )

    private val storage = mock<StorageService>()
    private val file = FileEntity(
        id = 555L,
        tenantId = 1111L,
        name = "T4-herve.pdf",
        url = "https://foo.com/1.pdf",
        contentType = "application/pdf",
    )
    private val fileContent = "Hello world"

    private val gemini = mock<LLM>()

    @BeforeEach
    fun setUp() {
        doReturn(
            configs.map { entry ->
                ConfigurationEntity(
                    name = entry.key,
                    value = entry.value.toString()
                )
            }
        ).whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())

        doReturn(file).whenever(fileService).get(any(), any())
        doReturn(storage).whenever(storageBuilder).build(any())
        doAnswer { inv ->
            val output = inv.getArgument<OutputStream>(1)
            IOUtils.copy(ByteArrayInputStream(fileContent.toByteArray()), output)
        }.whenever(storage).get(any(), any())

        doReturn(gemini).whenever(llmProvider).get(any())
    }

    @Test
    fun tearDown() {
        logger.log()
    }

    @Test
    fun init() {
        agent.setUp()
        verify(registry).register(agent)
    }

    @Test
    fun destroy() {
        agent.tearDown()
        verify(registry).unregister(agent)
    }

    @Test
    fun `handle event`() {
        // GIVEN
        val text = """
            {
                "type": "T5",
                "recipientName": "Ray Sponsible",
                "description": "This is a nice description"
            }
        """.trimIndent()
        doReturn(createLLMResponse(text)).whenever(gemini).generateContent(any())

        // WHEN
        val event = createFileUploadedEvent()
        val result = agent.notify(event)

        // THEN
        assertEquals(true, result)

        val request = argumentCaptor<LLMRequest>()
        verify(gemini).generateContent(request.capture())
        assertEquals(MediaType.APPLICATION_JSON, request.firstValue.config?.responseType)
        assertEquals(2, request.firstValue.messages.size)
        assertEquals(Role.MODEL, request.firstValue.messages[0].role)
        assertEquals(TaxAIAgent.SYSTEM_INSTRUCTION, request.firstValue.messages[0].text)
        assertEquals(null, request.firstValue.messages[0].document)
        assertEquals(Role.USER, request.firstValue.messages[1].role)
        assertEquals(TaxAIAgent.PROMPT, request.firstValue.messages[1].text)
        assertEquals(MediaType.valueOf(file.contentType), request.firstValue.messages[1].document?.contentType)
        assertNotNull(request.firstValue.messages[1].document?.content)

        verify(fileService).setLabels(file, listOf("T5"), "This is a nice description")
    }

    @Test
    fun `ignore event when AI not enabled`() {
        doReturn(
            configs.map { entry ->
                ConfigurationEntity(
                    name = entry.key,
                    value = entry.value.toString()
                )
            }.filter { config -> config.name != ConfigurationName.AI_PROVIDER }
        ).whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())

        val event = createFileUploadedEvent(ownerType = ObjectType.ACCOUNT)
        val result = agent.notify(event)

        assertEquals(true, result)
        verify(gemini, never()).generateContent(any())
    }

    @Test
    fun `ignore event when TAX AI Agent not enabled`() {
        doReturn(
            configs.map { entry ->
                ConfigurationEntity(
                    name = entry.key,
                    value = entry.value.toString()
                )
            }.filter { config -> config.name != ConfigurationName.TAX_AI_AGENT_ENABLED }
        ).whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())

        val event = createFileUploadedEvent(ownerType = ObjectType.ACCOUNT)
        val result = agent.notify(event)

        assertEquals(true, result)
        verify(gemini, never()).generateContent(any())
    }

    @Test
    fun `ignore non TAX events`() {
        val event = createFileUploadedEvent(ownerType = ObjectType.ACCOUNT)
        val result = agent.notify(event)

        assertEquals(true, result)
        verify(gemini, never()).generateContent(any())
    }

    @Test
    fun `ignore non FileDeletedEvent`() {
        val event = createFileUDeletedEvent()
        val result = agent.notify(event)

        assertEquals(false, result)
    }

    private fun createFileUploadedEvent(ownerType: ObjectType? = ObjectType.TAX): FileUploadedEvent {
        return FileUploadedEvent(
            fileId = file.id!!,
            tenantId = file.tenantId,
            owner = ownerType?.let { ObjectReference(id = 333L, type = ownerType) },
        )
    }

    private fun createFileUDeletedEvent(): FileDeletedEvent {
        return FileDeletedEvent(
            fileId = file.id!!,
            tenantId = file.tenantId
        )
    }

    private fun createLLMResponse(text: String): LLMResponse {
        return LLMResponse(
            messages = listOf(Message(text = text))
        )
    }
}
