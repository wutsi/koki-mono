package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.invoice.server.dao.InvoiceLogRepository
import com.wutsi.koki.invoice.server.dao.InvoiceRepository
import com.wutsi.koki.tax.server.dao.TaxRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/UpdateInvoiceStatusEndpoint.sql"])
class UpdateInvoiceStatusEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: InvoiceRepository

    @Autowired
    private lateinit var logDao: InvoiceLogRepository

    @Autowired
    private lateinit var taxDao: TaxRepository

    private fun test(invoiceId: Long, status: InvoiceStatus) {
        val request = UpdateInvoiceStatusRequest(
            status = status,
            comment = "Yo man"
        )
        val response = rest.postForEntity("/v1/invoices/$invoiceId/statuses", request, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val invoice = dao.findById(invoiceId).get()
        assertEquals(request.status, invoice.status)
        assertEquals(USER_ID, invoice.modifiedById)

        val logs = logDao.findByInvoice(invoice)
        assertEquals(1, logs.size)
        assertEquals(request.status, logs[0].status)
        assertEquals(request.comment, logs[0].comment)
        assertEquals(USER_ID, logs[0].createdById)
    }

    fun badStatus(invoiceId: Long, status: InvoiceStatus) {
        val request = UpdateInvoiceStatusRequest(
            status = status,
            comment = "Yo man"
        )
        val response = rest.postForEntity("/v1/invoices/$invoiceId/statuses", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.INVOICE_BAD_STATUS, response.body?.error?.code)

        val invoice = dao.findById(invoiceId).get()
        val logs = logDao.findByInvoice(invoice)
        assertEquals(0, logs.size)
    }

    @Test
    fun `DRAFT to OPENED`() {
        test(100, InvoiceStatus.OPENED)
    }

    @Test
    fun `DRAFT to VOIDED`() {
        test(101, InvoiceStatus.VOIDED)
    }

    @Test
    fun `DRAFT to PAID`() {
        badStatus(102, InvoiceStatus.PAID)
    }

    @Test
    fun `OPENED to PAID`() {
        test(110, InvoiceStatus.PAID)
    }

    @Test
    fun `OPENED to PAID with Balance`() {
        badStatus(111, InvoiceStatus.PAID)
    }

    @Test
    fun `OPENED to VOID`() {
        test(112, InvoiceStatus.VOIDED)
    }

    @Test
    fun `OPENED to DRAFT`() {
        badStatus(113, InvoiceStatus.DRAFT)
    }

    @Test
    fun `PAID to DRAFT`() {
        badStatus(120, InvoiceStatus.DRAFT)
    }

    @Test
    fun `PAID to OPENED`() {
        badStatus(121, InvoiceStatus.DRAFT)
    }

    @Test
    fun `PAID to VOID`() {
        badStatus(122, InvoiceStatus.DRAFT)
    }

    @Test
    fun `VOIDED to DRAFT`() {
        badStatus(130, InvoiceStatus.DRAFT)
    }

    @Test
    fun `VOIDED to OPENED`() {
        badStatus(131, InvoiceStatus.DRAFT)
    }

    @Test
    fun `VOIDED to VOID`() {
        badStatus(132, InvoiceStatus.DRAFT)
    }

    @Test
    fun `void tax invoice`() {
        test(140, InvoiceStatus.VOIDED)

        val tax = taxDao.findById(1400L).get()
        assertEquals(null, tax.invoiceId)
    }

    @Test
    fun unknown() {
        val invoiceId = 100L
        val request = UpdateInvoiceStatusRequest(
            status = InvoiceStatus.UNKNOWN,
            comment = "Yo man"
        )
        val response = rest.postForEntity("/v1/invoices/$invoiceId/statuses", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.INVOICE_BAD_STATUS, response.body?.error?.code)
    }
}
