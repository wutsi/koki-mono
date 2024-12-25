package com.wutsi.koki.form.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLFormGenerator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.io.StringWriter
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@Sql(value = ["/db/test/clean.sql", "/db/test/form/ExportFormHTMLDataEndpoint.sql"])
class ExportFormHTMLEndpointTest : AuthorizationAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    @MockitoBean
    private lateinit var generator: HTMLFormGenerator

    @Value("\${koki.portal-url}")
    private lateinit var portalUrl: String

    @Value("\${koki.server-url}")
    private lateinit var serverUrl: String

    private fun download(url: String, statusCode: Int, filename: String? = null): File? {
        return super.download(
            url,
            expectedFileName = filename,
            expectedStatusCode = statusCode,
            expectedContentType = "text/html",
            accessToken = createAccessToken()
        )
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doAnswer { inv ->
            val writer = inv.arguments[2] as StringWriter
            writer.write("<DIV>foo</DIV>")
        }.whenever(generator).generate(any<FormContent>(), any<Context>(), any<StringWriter>())
    }

    @Test
    fun `empty form`() {
        val url =
            "http://localhost:$port/v1/forms/html/1/100.html?&workflow-instance-id=xxx&activity-instance-id=yyy&read-only=true"

        val file = download(url, 200, "100.html")
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)

        val context = argumentCaptor<Context>()
        verify(generator).generate(any(), context.capture(), any())

        assertEquals(
            "http://localhost:8081/forms/100?workflow-instance-id=xxx&activity-instance-id=yyy",
            context.firstValue.submitUrl
        )
        assertEquals(2, context.firstValue.roleNames.size)
        assertTrue(context.firstValue.roleNames.contains("accountant"))
        assertTrue(context.firstValue.roleNames.contains("technician"))
        assertTrue(context.firstValue.readOnly)
        assertEquals(
            "$portalUrl/forms/100?workflow-instance-id=xxx&activity-instance-id=yyy",
            context.firstValue.submitUrl
        )
        assertEquals("$portalUrl/files", context.firstValue.downloadUrl)
        assertEquals(
            "$serverUrl/v1/files/upload?tenant-id=1&form-id=100&workflow-instance-id=xxx",
            context.firstValue.uploadUrl
        )

        assertEquals(0, context.firstValue.data.size)
    }

    @Test
    fun `form with data`() {
        val url = "http://localhost:$port/v1/forms/html/1/100/10011.html?&activity-instance-id=yyy"

        val file = download(url, 200, "10011.html")
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)

        val context = argumentCaptor<Context>()
        verify(generator).generate(any(), context.capture(), any())

        assertEquals("http://localhost:8081/forms/100/10011?activity-instance-id=yyy", context.firstValue.submitUrl)
        assertEquals(2, context.firstValue.roleNames.size)
        assertTrue(context.firstValue.roleNames.contains("accountant"))
        assertTrue(context.firstValue.roleNames.contains("technician"))
        assertFalse(context.firstValue.readOnly)
        assertEquals(TENANT_ID, context.firstValue.tenantId)
        assertEquals("$portalUrl/forms/100/10011?activity-instance-id=yyy", context.firstValue.submitUrl)
        assertEquals("$portalUrl/files", context.firstValue.downloadUrl)
        assertEquals("$serverUrl/v1/files/upload?tenant-id=1&form-id=100", context.firstValue.uploadUrl)

        assertEquals(2, context.firstValue.data.size)
        assertEquals(2, context.firstValue.data.size)
        assertEquals("aa", context.firstValue.data["A"])
        assertEquals("bb", context.firstValue.data["B"])
    }

    @Test
    fun `form data not found`() {
        val url = "http://localhost:$port/v1/forms/html/1/100/xxxx.html?&role-name=accountant"

        download(url, 404)
        verify(generator, never()).generate(any(), any(), any())
    }

    @Test
    fun `bad form data not found`() {
        val url = "http://localhost:$port/v1/forms/html/1/100/xxxx.html?&role-name=accountant"

        download(url, 404)
        verify(generator, never()).generate(any(), any(), any())
    }

    @Test
    fun `bad path`() {
        val url = "http://localhost:$port/v1/forms/html/1/110/xxxx.html?&role-name=accountant"

        download(url, 404)
        verify(generator, never()).generate(any(), any(), any())
    }
}
