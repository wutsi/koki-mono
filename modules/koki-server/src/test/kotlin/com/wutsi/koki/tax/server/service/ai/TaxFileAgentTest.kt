package com.wutsi.koki.tax.server.service.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.form.server.domain.FormEntity
import com.wutsi.koki.form.server.service.FormService
import com.wutsi.koki.platform.ai.llm.gemini.Gemini
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.tax.dto.TaxFileData
import com.wutsi.koki.tax.server.service.TaxMQConsumer
import com.wutsi.koki.tax.server.service.ai.TaxFileAgent.Companion.EXPENSE_CODE
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import java.io.File
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class TaxFileAgentTest {
    private val formService = mock<FormService>()

    private val directory = System.getProperty("user.home") + "/__wutsi"
    private val storage = LocalStorageService(
        directory = directory,
        baseUrl = "http://localhost:8080/storage"
    )

    private val account = AccountEntity(
        shippingCountry = "CA",
        tenantId = 111L,
    )

    private val forms = listOf(
        FormEntity(
            id = 1,
            code = "INT-T1",
            name = "LISTE DE CONTROLE - IMPOTS PERSONNEL",
            description = """
                This for is used for:
                 1) Collecting information about the client's household (client, spouse, children).
                 2) Collecting the list of fiscal form that will be provided for the tax declaration.
            """.trimIndent()
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

    private val agent = TaxFileAgent(
        account = account,
        formService = formService,
        llm = llm,
        maxIterations = 10,
    )

    @BeforeEach
    fun setUp() {
        Thread.sleep(12000) // Pause to support the 5 RPM limit from Gemini - see https://ai.google.dev/gemini-api/docs/rate-limits

        doReturn(forms).whenever(formService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
    }

    @Test
    fun `file uploaded - CA - T1`() {
        fileUploaded("T1", "/tax/ai/T1.pdf")
    }

    @Test
    fun `file uploaded - CA - T4`() {
        fileUploaded("T4", "/tax/ai/T4.pdf", expectedLanguage = null)
    }

    @Test
    fun `file uploaded - CA - T3-RCA`() {
        fileUploaded("T3-RCA", "/tax/ai/T3-RCA.pdf")
    }

    @Test
    fun `file uploaded - CA - TP-1`() {
        fileUploaded("TP-1.D", "/tax/ai/TP-1.D.pdf", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - CA - RL-1`() {
        fileUploaded("RL-1", "/tax/ai/RL-1.pdf", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - CA - RL-10`() {
        fileUploaded("RL-10", "/tax/ai/RL-10.png", "image/png", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - CA - RL-24`() {
        fileUploaded("RL-24", "/tax/ai/RL-24.pdf", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - CA - RL-3`() {
        fileUploaded("RL-3", "/tax/ai/RL-3.pdf", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - CA - RL-31`() {
        fileUploaded("RL-31", "/tax/ai/RL-31.pdf", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - medical invoice`() {
        fileUploaded(EXPENSE_CODE, "/tax/ai/medic.png", "image/png")
    }

    @Test
    fun `file uploaded - internal form INT-T1`() {
        fileUploaded("INT-T1", "/tax/ai/Control_List-Filled.pdf", expectedLanguage = "fr")
    }

    @Test
    fun `file uploaded - multi`() {
        // GIVEN
        val file = setupFile("/tax/ai/T1_RL1_medic.pdf", "application/pdf")

        // WHEN
        val result = agent.run(
            query = TaxMQConsumer.TAX_FILE_AGENT_QUERY,
            file = file
        )

        // THEN
        val data = ObjectMapper().readValue(result, TaxFileData::class.java)
        assertEquals("en", data.language)
        assertEquals(10, data.numberOfPages)
        assertEquals(3, data.sections.size)

        assertEquals(EXPENSE_CODE, data.sections[0].code)
        assertEquals(1, data.sections[0].startPage)
        assertEquals(1, data.sections[0].endPage)

        assertEquals("T1", data.sections[1].code)
        assertEquals(2, data.sections[1].startPage)
        assertEquals(9, data.sections[1].endPage)

        assertEquals("RL-1", data.sections[2].code)
        assertEquals(10, data.sections[2].startPage)
        assertEquals(10, data.sections[2].endPage)
    }

    private fun fileUploaded(
        expectedCode: String,
        path: String,
        contentType: String = "application/pdf",
        expectedLanguage: String? = "en"
    ) {
        // GIVEN
        val file = setupFile(path, contentType)

        // WHEN
        val result = agent.run(
            query = TaxMQConsumer.TAX_FILE_AGENT_QUERY,
            file = file
        )

        // THEN
        val data = ObjectMapper().readValue(result, TaxFileData::class.java)
        assertEquals(expectedCode, data.sections[0].code)
        assertEquals(1, data.sections.size)
        if (expectedLanguage != null) {
            assertEquals(expectedLanguage, data.language)
        }
    }

    private fun setupFile(path: String, contentType: String = "application/pdf"): File {
        val input = TaxFileAgentTest::class.java.getResourceAsStream(path)
        val extension = contentType.substring(contentType.indexOf("/") + 1)
        val path = "tax-ai-agent/" + UUID.randomUUID().toString() + "." + extension
        storage.store(path = path, content = input!!, contentType, -1)

        return File("$directory/$path")
    }
}
