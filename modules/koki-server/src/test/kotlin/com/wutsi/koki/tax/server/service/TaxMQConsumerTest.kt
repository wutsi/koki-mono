package com.wutsi.koki.tax.server.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.note.dto.event.NoteCreatedEvent
import com.wutsi.koki.note.dto.event.NoteDeletedEvent
import com.wutsi.koki.note.dto.event.NoteUpdatedEvent
import com.wutsi.koki.note.server.domain.NoteOwnerEntity
import com.wutsi.koki.note.server.service.NoteOwnerService
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.tax.dto.TaxFileData
import com.wutsi.koki.tax.dto.TaxFileSection
import com.wutsi.koki.tax.server.domain.TaxEntity
import com.wutsi.koki.tax.server.service.ai.TaxAgentFactory
import com.wutsi.koki.tax.server.service.ai.TaxFileAgent
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import kotlin.test.Test

class TaxMQConsumerTest {
    private val taxService = mock<TaxService>()
    private val noteOwnerService = mock<NoteOwnerService>()
    private val logger = DefaultKVLogger()
    private val fileService = mock<FileService>()
    private val storageServiceProvider = mock<StorageServiceProvider>()
    private val configurationService = mock<ConfigurationService>()
    private val taxFileService = mock<TaxFileService>()
    private val accountService = mock<AccountService>()
    private val taxAgentFactory = mock<TaxAgentFactory>()

    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val storage = LocalStorageService(
        directory = directory,
        baseUrl = "http://localhost:8080/storage"
    )

    private val consumer = TaxMQConsumer(
        taxService = taxService,
        noteOwnerService = noteOwnerService,
        logger = logger,
        objectMapper = ObjectMapper(),
        storageServiceProvider = storageServiceProvider,
        fileService = fileService,
        taxAgentFactory = taxAgentFactory,
        configurationService = configurationService,
        taxFileService = taxFileService,
        accountService = accountService,
    )

    val noteId = 111L
    val taxId = 222L
    val tenantId = 555L
    private val owner = NoteOwnerEntity(noteId = noteId, ownerId = taxId, ownerType = ObjectType.TAX)

    private val file = FileEntity(
        id = 111L,
        tenantId = tenantId,
        url = "http://localhost:8080/storage/TestFile.pdf",
        contentType = "application/pdf",
        name = "TestFile.pdf"
    )
    private val account = AccountEntity(
        id = 77L,
        tenantId = tenantId
    )
    private val tax = TaxEntity(
        id = 22L,
        accountId = account.id!!,
    )
    private val configs = mapOf(
        ConfigurationName.AI_PROVIDER to "GEMINI",
        ConfigurationName.TAX_AI_AGENT_ENABLED to "1"
    )

    @BeforeEach
    fun setUp() {
        doReturn(listOf(owner)).whenever(noteOwnerService).findByNoteIdAndOwnerType(noteId, ObjectType.TAX)
        doReturn(storage).whenever(storageServiceProvider).get(any())
        doReturn(file).whenever(fileService).get(any(), any())
        doReturn(tax).whenever(taxService).get(any(), any())
        doReturn(account).whenever(accountService).get(any(), any())

        doReturn(
            configs.map { entry -> ConfigurationEntity(name = entry.key, value = entry.value) }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())
    }

    @AfterEach
    fun tearDown() {
        logger.log()
    }

    @Test
    fun `note created`() {
        consumer.consume(NoteCreatedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService).updateMetrics(taxId, tenantId)
    }

    @Test
    fun `note updated`() {
        consumer.consume(NoteUpdatedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService).updateMetrics(taxId, tenantId)
    }

    @Test
    fun `note deleted`() {
        consumer.consume(NoteDeletedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService).updateMetrics(taxId, tenantId)
    }

    @Test
    fun `note not owner by TAX`() {
        doReturn(emptyList<NoteOwnerEntity>()).whenever(noteOwnerService)
            .findByNoteIdAndOwnerType(noteId, ObjectType.TAX)

        consumer.consume(NoteDeletedEvent(noteId = noteId, tenantId = tenantId))
        verify(taxService, never()).updateMetrics(any(), any())
    }

    @Test
    fun `file uploaded to tax`() {
        // GIVEN
        setupFile("/tax/ai/RL-1.pdf")

        val data = TaxFileData(numberOfPages = 1, language = "en", sections = listOf(TaxFileSection(code = "T4")))
        val agent = mock<TaxFileAgent>()
        doReturn(ObjectMapper().writeValueAsString(data)).whenever(agent).run(any(), anyOrNull())
        doReturn(agent).whenever(taxAgentFactory).createTaxFileAgent(any())

        // WHEN
        val event = FileUploadedEvent(
            fileId = 111,
            owner = ObjectReference(id = tax.id!!, type = ObjectType.TAX)
        )
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        verify(taxFileService).save(file, data)
    }

    @Test
    fun `file uploaded to tax - no AI provider`() {
        // GIVEN
        doReturn(
            configs.map { entry -> ConfigurationEntity(name = entry.key, value = entry.value) }
                .filter { cfg -> cfg.name != ConfigurationName.AI_PROVIDER }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

        // WHEN
        val event = FileUploadedEvent(
            fileId = 111,
            owner = ObjectReference(id = tax.id!!, type = ObjectType.TAX)
        )
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        verify(taxFileService, never()).save(any(), any())
        verify(taxAgentFactory, never()).createTaxFileAgent(any())
    }

    @Test
    fun `file uploaded to tax - Tax AI not enabled`() {
        // GIVEN
        doReturn(
            configs.map { entry -> ConfigurationEntity(name = entry.key, value = entry.value) }
                .filter { cfg -> cfg.name != ConfigurationName.TAX_AI_AGENT_ENABLED }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

        // WHEN
        val event = FileUploadedEvent(
            fileId = 111,
            owner = ObjectReference(id = tax.id!!, type = ObjectType.TAX)
        )
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        verify(taxFileService, never()).save(any(), any())
        verify(taxAgentFactory, never()).createTaxFileAgent(any())
    }

    @Test
    fun `file uploaded to account`() {
        // GIVEN
        doReturn(
            configs.map { entry -> ConfigurationEntity(name = entry.key, value = entry.value) }
                .filter { cfg -> cfg.name != ConfigurationName.TAX_AI_AGENT_ENABLED }
        ).whenever(configurationService).search(any(), anyOrNull(), anyOrNull())

        // WHEN
        val event = FileUploadedEvent(
            fileId = 111,
            owner = ObjectReference(id = account.id!!, type = ObjectType.ACCOUNT)
        )
        val result = consumer.consume(event)

        // THEN
        assertTrue(result)

        verify(taxFileService, never()).save(any(), any())
        verify(taxAgentFactory, never()).createTaxFileAgent(any())
    }

    @Test
    fun `event not supported`() {
        consumer.consume(FileDeletedEvent())
        verify(taxService, never()).updateMetrics(any(), any())
    }

    private fun setupFile(path: String, contentType: String = "application/pdf"): File {
        val input = TaxMQConsumer::class.java.getResourceAsStream(path)
        storage.store(path = file.name, content = input!!, contentType, -1)

        return File("$directory/$path")
    }
}
