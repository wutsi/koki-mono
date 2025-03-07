package com.wutsi.koki.invoice.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.invoice.dto.InvoiceStatus
import com.wutsi.koki.invoice.dto.UpdateInvoiceStatusRequest
import com.wutsi.koki.invoice.dto.event.InvoiceStatusChangedEvent
import com.wutsi.koki.invoice.server.dao.InvoiceLogRepository
import com.wutsi.koki.invoice.server.dao.InvoiceRepository
import com.wutsi.koki.invoice.server.domain.InvoiceEntity
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tax.server.dao.TaxRepository
import com.wutsi.koki.tenant.dto.ConfigurationName
import com.wutsi.koki.tenant.dto.SaveConfigurationRequest
import com.wutsi.koki.tenant.server.service.ConfigurationService
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import java.util.Date
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

    @Autowired
    private lateinit var fileDao: FileRepository

    @Autowired
    private lateinit var configService: ConfigurationService

    @MockitoBean
    private lateinit var publisher: Publisher

    private fun test(invoiceId: Long, status: InvoiceStatus): InvoiceEntity {
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

        val event = argumentCaptor<InvoiceStatusChangedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(status, event.firstValue.status)
        assertEquals(TENANT_ID, event.firstValue.tenantId)
        assertEquals(invoiceId, event.firstValue.invoiceId)

        return invoice
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
    fun `DRAFT to OPENED - no due days`() {
        configService.save(
            request = SaveConfigurationRequest(
                mapOf(ConfigurationName.INVOICE_DUE_DAYS to "")
            ),
            tenantId = TENANT_ID
        )

        val invoice = test(100, InvoiceStatus.OPENED)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val now = Date()
        assertEquals(fmt.format(now), fmt.format(invoice.invoicedAt))
        assertEquals(fmt.format(now), fmt.format(invoice.dueAt))
    }

    @Test
    fun `DRAFT to OPENED - 30 due days`() {
        configService.save(
            request = SaveConfigurationRequest(
                mapOf(ConfigurationName.INVOICE_DUE_DAYS to "30")
            ),
            tenantId = TENANT_ID
        )

        val invoice = test(101, InvoiceStatus.OPENED)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val now = Date()
        assertEquals(fmt.format(now), fmt.format(invoice.invoicedAt))
        assertEquals(fmt.format(DateUtils.addDays(now, 30)), fmt.format(invoice.dueAt))
    }

    @Test
    fun `DRAFT to OPENED - invalid due days`() {
        configService.save(
            request = SaveConfigurationRequest(
                mapOf(ConfigurationName.INVOICE_DUE_DAYS to "xxx")
            ),
            tenantId = TENANT_ID
        )

        val invoice = test(102, InvoiceStatus.OPENED)

        val fmt = SimpleDateFormat("yyyy-MM-dd")
        val now = Date()
        assertEquals(fmt.format(now), fmt.format(invoice.invoicedAt))
        assertEquals(fmt.format(now), fmt.format(invoice.dueAt))
    }

    @Test
    fun `DRAFT to VOIDED`() {
        test(103, InvoiceStatus.VOIDED)
    }

    @Test
    fun `DRAFT to PAID`() {
        badStatus(104, InvoiceStatus.PAID)
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
