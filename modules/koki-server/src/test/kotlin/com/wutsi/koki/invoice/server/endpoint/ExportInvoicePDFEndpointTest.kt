package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/ExportInvoicePDFEndpoint.sql"])
class ExportInvoicePDFEndpointTest : AuthorizationAwareEndpointTest() {
    @LocalServerPort
    private lateinit var port: Integer

    @Test
    fun invoice() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/invoice-$TENANT_ID.100.pdf",
            expectedStatusCode = 200,
            expectedFileName = "invoice-$TENANT_ID.100.pdf",
            expectedContentType = "application/pdf"
        )

        assertNotNull(file)
        assertTrue(file.exists())
        assertTrue(file.length() > 0L)
    }

    @Test
    fun `draft invoice`() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/invoice-$TENANT_ID.101.pdf",
            expectedStatusCode = 404,
            expectedFileName = null,
            expectedContentType = "",
        )

        assertNull(file)
    }

    @Test
    fun `voided invoice`() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/invoice-$TENANT_ID.102.pdf",
            expectedStatusCode = 200,
            expectedFileName = "invoice-$TENANT_ID.102.pdf",
            expectedContentType = "application/pdf"
        )

        assertNotNull(file)
        assertTrue(file.exists())
        assertTrue(file.length() > 0L)
    }

    @Test
    fun receipt() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/receipt-$TENANT_ID.200.pdf",
            expectedStatusCode = 200,
            expectedFileName = "receipt-$TENANT_ID.200.pdf",
            expectedContentType = "application/pdf"
        )

        assertNotNull(file)
        assertTrue(file.exists())
        assertTrue(file.length() > 0L)
    }

    @Test
    fun notFound() {
        val file = download(
            url = "http://localhost:$port/v1/invoices/pdf/invoice-$TENANT_ID.9999.pdf",
            expectedStatusCode = 404,
            expectedFileName = null,
            expectedContentType = ""
        )
        assertNull(file)
    }
}
