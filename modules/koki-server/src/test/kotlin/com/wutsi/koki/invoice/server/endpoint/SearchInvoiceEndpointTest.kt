package com.wutsi.koki.invoice.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.invoice.dto.SearchInvoiceResponse
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/invoice/SearchInvoiceEndpoint.sql"])
class SearchInvoiceEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/invoices", SearchInvoiceResponse::class.java)

        val invoices = response.body!!.invoices
        assertEquals(4, invoices.size)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/invoices?id=100&id=102&id=103&id=200", SearchInvoiceResponse::class.java)

        val invoices = response.body!!.invoices
        assertEquals(3, invoices.size)
        assertEquals(listOf(103L, 102L, 100L), invoices.map { invoice -> invoice.id })
    }

    @Test
    fun `by tax-id`() {
        val response = rest.getForEntity("/v1/invoices?tax-id=7779", SearchInvoiceResponse::class.java)

        val invoices = response.body!!.invoices
        assertEquals(1, invoices.size)
        assertEquals(102, invoices[0].id)
    }

    @Test
    fun `by order-id`() {
        val response = rest.getForEntity("/v1/invoices?order-id=8888", SearchInvoiceResponse::class.java)

        val invoices = response.body!!.invoices
        assertEquals(1, invoices.size)
        assertEquals(103, invoices[0].id)
    }

    @Test
    fun `by number`() {
        val response = rest.getForEntity("/v1/invoices?number=10958", SearchInvoiceResponse::class.java)

        val invoices = response.body!!.invoices
        assertEquals(1, invoices.size)
        assertEquals(103, invoices[0].id)
    }

    @Test
    fun `by status`() {
        val response =
            rest.getForEntity("/v1/invoices?status=OPENED&status=VOIDED", SearchInvoiceResponse::class.java)

        val invoices = response.body!!.invoices
        assertEquals(2, invoices.size)
        assertEquals(listOf(103L, 100L), invoices.map { invoice -> invoice.id })
    }
}
