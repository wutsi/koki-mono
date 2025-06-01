package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.invoice.dto.GetInvoiceResponse
import com.wutsi.koki.invoice.dto.InvoiceStatus
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.text.SimpleDateFormat
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/GetInvoicePDFEndpoint.sql"])
class GetInvoiceEndpointTest : AuthorizationAwareEndpointTest() {
    private val fmt = SimpleDateFormat("yyyy-MM-dd")

    @Test
    fun get() {
        val response = rest.getForEntity("/v1/invoices/100", GetInvoiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val invoice = response.body!!.invoice
        assertEquals(10955L, invoice.number)
        assertEquals(9999L, invoice.orderId)
        assertEquals(InvoiceStatus.OPENED, invoice.status)
        assertEquals("Sample description", invoice.description)
        assertEquals(111, invoice.customer.accountId)
        assertEquals("Ray Sponsible", invoice.customer.name)
        assertEquals("ray.sponsible@gmail.com", invoice.customer.email)
        assertEquals("+5147580111", invoice.customer.phone)
        assertEquals("+514758000", invoice.customer.mobile)
        assertEquals(40.00, invoice.totalTaxAmount)
        assertEquals(20.00, invoice.totalDiscountAmount)
        assertEquals(800.00, invoice.subTotalAmount)
        assertEquals(820.00, invoice.totalAmount)
        assertEquals(810.00, invoice.amountPaid)
        assertEquals(10.00, invoice.amountDue)
        assertEquals("CAD", invoice.currency)
        assertEquals(111L, invoice.shippingAddress?.cityId)
        assertEquals(100L, invoice.shippingAddress?.stateId)
        assertEquals("CA", invoice.shippingAddress?.country)
        assertEquals("340 Pascal", invoice.shippingAddress?.street)
        assertEquals("H1K1C1", invoice.shippingAddress?.postalCode)
        assertEquals(211L, invoice.billingAddress?.cityId)
        assertEquals(200L, invoice.billingAddress?.stateId)
        assertEquals("CA", invoice.billingAddress?.country)
        assertEquals("311 Pascal", invoice.billingAddress?.street)
        assertEquals("H2K2C2", invoice.billingAddress?.postalCode)
        assertEquals("2025-01-01", fmt.format(invoice.invoicedAt))
        assertEquals("2025-01-30", fmt.format(invoice.dueAt))

        val items = invoice.items
        assertEquals(2, items.size)
        assertEquals(1L, items[0].productId)
        assertEquals(11L, items[0].unitPriceId)
        assertEquals(110L, items[0].unitId)
        assertEquals(300.0, items[0].unitPrice)
        assertEquals(2, items[0].quantity)
        assertEquals(600.0, items[0].subTotal)
        assertEquals("product 1", items[0].description)
        assertEquals(2L, items[1].productId)
        assertEquals(22L, items[1].unitPriceId)
        assertEquals(111L, items[1].unitId)
        assertEquals(200.0, items[1].unitPrice)
        assertEquals(1, items[1].quantity)
        assertEquals(200.0, items[1].subTotal)
        assertEquals("product 2", items[1].description)

        val taxes0 = items[0].taxes
        assertEquals(2, taxes0.size)
        assertEquals(1011L, taxes0[0].salesTaxId)
        assertEquals(5.0, taxes0[0].rate)
        assertEquals(10.0, taxes0[0].amount)
        assertEquals("CAD", taxes0[0].currency)
        assertEquals(1112L, taxes0[1].salesTaxId)
        assertEquals(9.975, taxes0[1].rate)
        assertEquals(25.00, taxes0[1].amount)
        assertEquals("CAD", taxes0[1].currency)

        val taxes1 = items[1].taxes
        assertEquals(1, taxes1.size)
        assertEquals(1011L, taxes1[0].salesTaxId)
        assertEquals(5.0, taxes1[0].rate)
        assertEquals(5.0, taxes1[0].amount)
        assertEquals("CAD", taxes1[0].currency)

        val taxes = invoice.taxes
        assertEquals(2, taxes.size)
        assertEquals(1011L, taxes[0].salesTaxId)
        assertEquals(5.0, taxes[0].rate)
        assertEquals(15.0, taxes[0].amount)
        assertEquals("CAD", taxes[0].currency)
        assertEquals(1112L, taxes[1].salesTaxId)
        assertEquals(9.975, taxes[1].rate)
        assertEquals(25.0, taxes[1].amount)
        assertEquals("CAD", taxes[1].currency)
    }

    @Test
    fun `get with paynow-id`() {
        val response = rest.getForEntity("/v1/invoices/100?paynow-id=paynow100", GetInvoiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val invoice = response.body!!.invoice
        assertEquals(10955L, invoice.number)
        assertEquals(9999L, invoice.orderId)
        assertEquals(InvoiceStatus.OPENED, invoice.status)
        assertEquals("Sample description", invoice.description)
        assertEquals(111, invoice.customer.accountId)
        assertEquals("Ray Sponsible", invoice.customer.name)
        assertEquals("ray.sponsible@gmail.com", invoice.customer.email)
        assertEquals("+5147580111", invoice.customer.phone)
        assertEquals("+514758000", invoice.customer.mobile)
        assertEquals(40.00, invoice.totalTaxAmount)
        assertEquals(20.00, invoice.totalDiscountAmount)
        assertEquals(800.00, invoice.subTotalAmount)
        assertEquals(820.00, invoice.totalAmount)
        assertEquals(810.00, invoice.amountPaid)
        assertEquals(10.00, invoice.amountDue)
        assertEquals("CAD", invoice.currency)
        assertEquals(111L, invoice.shippingAddress?.cityId)
        assertEquals(100L, invoice.shippingAddress?.stateId)
        assertEquals("CA", invoice.shippingAddress?.country)
        assertEquals("340 Pascal", invoice.shippingAddress?.street)
        assertEquals("H1K1C1", invoice.shippingAddress?.postalCode)
        assertEquals(211L, invoice.billingAddress?.cityId)
        assertEquals(200L, invoice.billingAddress?.stateId)
        assertEquals("CA", invoice.billingAddress?.country)
        assertEquals("311 Pascal", invoice.billingAddress?.street)
        assertEquals("H2K2C2", invoice.billingAddress?.postalCode)
        assertEquals("2025-01-01", fmt.format(invoice.invoicedAt))
        assertEquals("2025-01-30", fmt.format(invoice.dueAt))

        val items = invoice.items
        assertEquals(2, items.size)
        assertEquals(1L, items[0].productId)
        assertEquals(11L, items[0].unitPriceId)
        assertEquals(110L, items[0].unitId)
        assertEquals(300.0, items[0].unitPrice)
        assertEquals(2, items[0].quantity)
        assertEquals(600.0, items[0].subTotal)
        assertEquals("product 1", items[0].description)
        assertEquals(2L, items[1].productId)
        assertEquals(22L, items[1].unitPriceId)
        assertEquals(111L, items[1].unitId)
        assertEquals(200.0, items[1].unitPrice)
        assertEquals(1, items[1].quantity)
        assertEquals(200.0, items[1].subTotal)
        assertEquals("product 2", items[1].description)

        val taxes0 = items[0].taxes
        assertEquals(2, taxes0.size)
        assertEquals(1011L, taxes0[0].salesTaxId)
        assertEquals(5.0, taxes0[0].rate)
        assertEquals(10.0, taxes0[0].amount)
        assertEquals("CAD", taxes0[0].currency)
        assertEquals(1112L, taxes0[1].salesTaxId)
        assertEquals(9.975, taxes0[1].rate)
        assertEquals(25.00, taxes0[1].amount)
        assertEquals("CAD", taxes0[1].currency)

        val taxes1 = items[1].taxes
        assertEquals(1, taxes1.size)
        assertEquals(1011L, taxes1[0].salesTaxId)
        assertEquals(5.0, taxes1[0].rate)
        assertEquals(5.0, taxes1[0].amount)
        assertEquals("CAD", taxes1[0].currency)

        val taxes = invoice.taxes
        assertEquals(2, taxes.size)
        assertEquals(1011L, taxes[0].salesTaxId)
        assertEquals(5.0, taxes[0].rate)
        assertEquals(15.0, taxes[0].amount)
        assertEquals("CAD", taxes[0].currency)
        assertEquals(1112L, taxes[1].salesTaxId)
        assertEquals(9.975, taxes[1].rate)
        assertEquals(25.0, taxes[1].amount)
        assertEquals("CAD", taxes[1].currency)
    }

    @Test
    fun `invalid paynow-id`() {
        val response = rest.getForEntity("/v1/invoices/100?paynow-id=xxx", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.INVOICE_NOT_FOUND, response.body?.error?.code)
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/invoices/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.INVOICE_NOT_FOUND, response.body?.error?.code)
    }
}
