package com.wutsi.koki.invoice.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.invoice.server.command.SendInvoiceCommand
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/SendInvoicePDFEndpoint.sql"])
class SendInvoiceEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun get() {
        val response = rest.getForEntity("/v1/invoices/100/send", Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val cmd = argumentCaptor<SendInvoiceCommand>()
        verify(publisher).publish(cmd.capture())
        assertEquals(100L, cmd.firstValue.invoiceId)
        assertEquals(TENANT_ID, cmd.firstValue.tenantId)
    }

    @Test
    fun draft() {
        val response = rest.getForEntity("/v1/invoices/200/send", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.INVOICE_BAD_STATUS, response.body?.error?.code)

        verify(publisher, never()).publish(any())
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/invoices/999/send", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.INVOICE_NOT_FOUND, response.body?.error?.code)

        verify(publisher, never()).publish(any())
    }
}
