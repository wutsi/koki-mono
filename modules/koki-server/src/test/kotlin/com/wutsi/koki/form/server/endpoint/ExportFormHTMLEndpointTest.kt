package com.wutsi.koki.tenant.server.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doAnswer
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
import java.net.URLEncoder
import javax.imageio.ImageIO
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/form/ExportFormHTMLDataEndpoint.sql"])
class ExportFormHTMLEndpointTest : TenantAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    @MockBean
    private lateinit var generator: HTMLFormGenerator

    private fun download(url: String, statusCode: Int): File? {
        val i = url.lastIndexOf("/")
        val j = url.lastIndexOf("?")
        val filename = if (j > 0) url.substring(i + 1, j) else url.substring(i + 1)

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
    fun `html from activity instance`() {
        val url = "http://localhost:$port/v1/forms/html/1.100.html" +
            "?aiid=wi-100-01-working" +
            "&role-name=accountant" +
            "&submit-url=" + URLEncoder.encode("https://localhost:8081/100/submit", "utf-8")

        val file = download(url, 200)
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)

        val context = argumentCaptor<Context>()
        verify(generator).generate(any(), context.capture(), any())

        assertEquals("https://localhost:8081/100/submit", context.firstValue.submitUrl)
        assertEquals("accountant", context.firstValue.roleName)
        assertEquals(2, context.firstValue.data.size)
        assertEquals("Ray Sponsible", context.firstValue.data["customer_name"])
        assertEquals("ray.sponsible@gmail.com", context.firstValue.data["customer_email"])
    }

    @Test
    fun `html without activity instance`() {
        val url = "http://localhost:$port/v1/forms/html/1.100.html" +
            "?role-name=accountant" +
            "&submit-url=" + URLEncoder.encode("https://localhost:8081/100/submit", "utf-8")

        val file = download(url, 200)
        assertTrue(file!!.length() > 0L)
        assertTrue(file.length() > 0)
        ImageIO.read(file)

        val context = argumentCaptor<Context>()
        verify(generator).generate(any(), context.capture(), any())

        assertEquals("https://localhost:8081/100/submit", context.firstValue.submitUrl)
        assertEquals("accountant", context.firstValue.roleName)
        assertEquals(0, context.firstValue.data.size)
    }
}
