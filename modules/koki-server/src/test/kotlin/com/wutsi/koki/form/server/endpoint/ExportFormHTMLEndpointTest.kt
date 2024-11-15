package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.form.dto.FormContent
import com.wutsi.koki.form.server.generator.html.Context
import com.wutsi.koki.form.server.generator.html.HTMLFormGenerator
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import java.io.File
import java.io.StringWriter
import javax.imageio.ImageIO
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/ExportFormHTMLDataEndpoint.sql"])
class ExportFormHTMLEndpointTest : TenantAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    @MockBean
    private lateinit var generator: HTMLFormGenerator

    private fun download(url: String, statusCode: Int, filename: String? = null): File? {
        return super.download(
            url,
            expectedFileName = filename,
            expectedStatusCode = statusCode,
            expectedContentType = "text/html"
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
            "http://localhost:$port/v1/forms/html/1/100.html?&role-name=accountant&workflow-instance-id=xxx&activity-instance-id=yyy"

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
        assertEquals("accountant", context.firstValue.roleName)
        assertEquals(0, context.firstValue.data.size)
    }

    @Test
    fun `form with data`() {
        val url = "http://localhost:$port/v1/forms/html/1/100/10011.html?&role-name=accountant&activity-instance-id=yyy"

        val file = download(url, 200, "10011.html")
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)

        val context = argumentCaptor<Context>()
        verify(generator).generate(any(), context.capture(), any())

        assertEquals("http://localhost:8081/forms/100/10011?activity-instance-id=yyy", context.firstValue.submitUrl)
        assertEquals("accountant", context.firstValue.roleName)
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
