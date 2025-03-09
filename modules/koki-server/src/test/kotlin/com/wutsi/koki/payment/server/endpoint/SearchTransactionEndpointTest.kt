package com.wutsi.koki.payment.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.payment.dto.SearchTransactionResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/payment/SearchTransactionEndpoint.sql"])
class SearchTransactionEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/transactions", SearchTransactionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transactions
        assertEquals(5, tx.size)
    }

    @Test
    fun `by invoice`() {
        val response = rest.getForEntity("/v1/transactions?invoice-id=555", SearchTransactionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transactions
        assertEquals(listOf("100", "110"), tx.map { it.id }.sorted())
    }

    @Test
    fun `by type`() {
        val response =
            rest.getForEntity("/v1/transactions?type=PAYMENT&type=CHARGE", SearchTransactionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transactions
        assertEquals(listOf("120", "121", "130"), tx.map { it.id }.sorted())
    }

    @Test
    fun `by date`() {
        val response =
            rest.getForEntity(
                "/v1/transactions?created-at-from=2020-01-01&created-at-to=2020-01-31",
                SearchTransactionResponse::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val tx = response.body!!.transactions
        assertEquals(listOf("100", "110"), tx.map { it.id }.sorted())
    }
}
