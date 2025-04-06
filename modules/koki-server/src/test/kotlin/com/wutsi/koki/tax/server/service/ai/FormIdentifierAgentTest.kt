package com.wutsi.koki.tax.server.service.ai

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.account.server.service.AccountService
import com.wutsi.koki.ai.server.service.AIMQConsumer
import com.wutsi.koki.ai.server.service.LLMProvider
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.event.FileDeletedEvent
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.domain.FileEntity
import com.wutsi.koki.file.server.service.FileService
import com.wutsi.koki.file.server.service.StorageServiceProvider
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.platform.ai.llm.LLMType
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.platform.logger.DefaultKVLogger
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.tax.server.domain.TaxEntity
import com.wutsi.koki.tax.server.service.TaxService
import com.wutsi.koki.tax.server.service.ai.FormIdentifierAgent.Companion.EXPENSE_CODE
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.server.domain.ConfigurationEntity
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class FormIdentifierAgentTest {
    private val registry = mock<AIMQConsumer>()
    private val llmProvider = mock<LLMProvider>()
    private val fileService = mock<FileService>()
    private val storageServiceProvider = mock<StorageServiceProvider>()
    private val configurationService = mock<ConfigurationService>()
    private val accountService = mock<AccountService>()
    private val formService = mock<FormService>()
    private val taxService = mock<TaxService>()
    private val logger = DefaultKVLogger()
    private val agent = FormIdentifierAgent(
        accountService = accountService,
        fileService = fileService,
        taxService = taxService,
        formService = formService,
        configurationService = configurationService,
        storageServiceProvider = storageServiceProvider,
        llmProvider = llmProvider,
        registry = registry,
        logger = logger,
    )

    private val account = AccountEntity(
        id = 777L,
        shippingCountry = "CA"
    )

    private val tax = TaxEntity(
        id = 333L,
        accountId = account.id!!
    )

    private val configs = mapOf(
        ConfigurationName.AI_PROVIDER to LLMType.GEMINI,
        ConfigurationName.TAX_AI_AGENT_ENABLED to "1",
    )

    private val storage = LocalStorageService(
        directory = System.getProperty("user.home") + "/__wutsi",
        baseUrl = "http://localhost:8080/storage"
    )

    private val forms = listOf(
        FormEntity(
            id = 1,
            code = "INT-T1",
            name = "Client information and and list of documents"
        ),
        FormEntity(
            id = 1,
            code = "INT-T2",
            name = "Business Information"
        ),
    )

    private val llm = Gemini(
        apiKey = System.getenv("GEMINI_API_KEY"),
        model = "gemini-2.0-flash",
    )

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

        doReturn(account).whenever(accountService).get(any(), any())
        doReturn(tax).whenever(taxService).get(any(), any())
        doReturn(storage).whenever(storageServiceProvider).get(any())
        doReturn(llm).whenever(llmProvider).get(any())
        doReturn(forms).whenever(formService).search(
            any(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
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
    fun `file uploaded - CA - T1`() {
        fileUploaded("T1", "/tax/ai/T1.pdf")
    }

    @Test
    fun `file uploaded - CA - T4`() {
        fileUploaded("T4", "/tax/ai/T4.pdf")
    }

    @Test
    fun `file uploaded - CA - T3-RCA`() {
        fileUploaded("T3-RCA", "/tax/ai/T3-RCA.pdf")
    }

    @Test
    fun `file uploaded - CA - TP-1`() {
        fileUploaded("TP-1.D", "/tax/ai/TP-1.D.pdf")
    }

    @Test
    fun `file uploaded - CA - RL-1`() {
        fileUploaded("RL-1", "/tax/ai/RL-1.pdf")
    }

    @Test
    fun `file uploaded - CA - RL-10`() {
        fileUploaded("RL-10", "/tax/ai/RL-10.png", "image/png")
    }

    @Test
    fun `file uploaded - CA - RL-24`() {
        fileUploaded("RL-24", "/tax/ai/RL-24.pdf")
    }

    @Test
    fun `file uploaded - CA - RL-3`() {
        fileUploaded("RL-3", "/tax/ai/RL-3.pdf")
    }

    @Test
    fun `file uploaded - CA - RL-31`() {
        fileUploaded("RL-31", "/tax/ai/RL-31.pdf")
    }

    @Test
    fun `file uploaded - medical invoice`() {
        fileUploaded(EXPENSE_CODE, "/tax/ai/medic.png", "image/png")
    }

    @Test
    fun `file uploaded - internal form INT-T1`() {
        fileUploaded("INT-T1", "/tax/ai/Control_List-Filled.pdf")
    }

    private fun fileUploaded(expectedLabel: String, path: String, contentType: String = "application/pdf") {
        // GIVEN
        val file = setupFile(path, contentType)

        // WHEN
        val event = createFileUploadedEvent(file)
        val result = agent.notify(event)

        // THEN
        assertEquals(true, result)
        verify(fileService).setLabels(eq(file), eq(listOf(expectedLabel)), anyOrNull())
    }

    @Test
    fun `file uploaded - AI not enabled`() {
        doReturn(
            configs.map { entry ->
                ConfigurationEntity(
                    name = entry.key,
                    value = entry.value.toString()
                )
            }.filter { config -> config.name != ConfigurationName.AI_PROVIDER }
        ).whenever(configurationService)
            .search(any(), anyOrNull(), anyOrNull())

        val file = setupFile("/tax/ai/Control_List-Filled.pdf")

        val event = createFileUploadedEvent(file)
        val result = agent.notify(event)

        assertEquals(true, result)
        verify(fileService, never()).setLabels(any(), any(), anyOrNull())
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

        val file = setupFile("/tax/ai/Control_List-Filled.pdf")

        val event = createFileUploadedEvent(file)
        val result = agent.notify(event)

        assertEquals(true, result)
        verify(fileService, never()).setLabels(any(), any(), anyOrNull())
    }

    @Test
    fun `ignore non TAX events`() {
        val file = setupFile("/tax/ai/Control_List-Filled.pdf")

        val event = createFileUploadedEvent(file, ownerType = ObjectType.ACCOUNT)
        val result = agent.notify(event)

        assertEquals(true, result)
        verify(fileService, never()).setLabels(any(), any(), anyOrNull())
    }

    @Test
    fun `ignore non FileDeletedEvent`() {
        val event = createFileUDeletedEvent()
        val result = agent.notify(event)

        assertEquals(false, result)
    }

    private fun setupFile(path: String, contentType: String = "application/pdf"): FileEntity {
        val input = FormIdentifierAgentTest::class.java.getResourceAsStream(path)
        val path = "tax-ai-agent/" + UUID.randomUUID().toString()
        val url = storage.store(path = path, content = input!!, contentType, -1)

        val file = FileEntity(
            id = 555L,
            tenantId = 1111L,
            name = url.file,
            url = url.toString(),
            contentType = contentType,
        )
        doReturn(file).whenever(fileService).get(any(), any())
        return file
    }

    private fun createFileUploadedEvent(
        file: FileEntity,
        ownerId: Long = tax.id!!,
        ownerType: ObjectType? = ObjectType.TAX
    ): FileUploadedEvent {
        return FileUploadedEvent(
            fileId = file.id!!,
            tenantId = file.tenantId,
            owner = ownerType?.let { ObjectReference(id = ownerId, type = ownerType) },
        )
    }

    private fun createFileUDeletedEvent(): FileDeletedEvent {
        return FileDeletedEvent(
            fileId = 11L,
            tenantId = 222L
        )
    }
}
