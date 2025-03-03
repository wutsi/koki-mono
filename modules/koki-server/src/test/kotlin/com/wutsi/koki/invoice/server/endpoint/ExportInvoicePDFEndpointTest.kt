package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/ExportInvoicePDFEndpoint.sql"])
class ExportInvoicePDFEndpointTest : AuthorizationAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    @Test
    fun export() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/$TENANT_ID.100.pdf",
            expectedStatusCode = 200,
            expectedFileName = "$TENANT_ID.100.pdf",
            expectedContentType = "application/pdf"
        )

        assertTrue(file!!.length() > 0L)
    }

    @Test
    fun paid() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/$TENANT_ID.200.pdf",
            expectedStatusCode = 200,
            expectedFileName = "$TENANT_ID.200.pdf",
            expectedContentType = "application/pdf"
        )

        assertTrue(file!!.length() > 0L)
    }

    @Test
    fun cancelled() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/$TENANT_ID.300.pdf",
            expectedStatusCode = 200,
            expectedFileName = "$TENANT_ID.300.pdf",
            expectedContentType = "application/pdf"
        )

        assertTrue(file!!.length() > 0L)
    }

    @Test
    fun notFound() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/$TENANT_ID.9999.pdf",
            expectedStatusCode = 404,
            expectedFileName = null,
            expectedContentType = ""
        )
        assertNull(file)
    }
}
