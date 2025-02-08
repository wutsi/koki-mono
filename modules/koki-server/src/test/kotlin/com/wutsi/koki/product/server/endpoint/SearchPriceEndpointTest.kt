package com.wutsi.koki.price.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.product.dto.SearchPriceResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/SearchPriceEndpoint.sql"])
class SearchPriceEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun search() {
        val response = rest.getForEntity("/v1/prices", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(4, prices.size)
    }

    @Test
    fun `by currency`() {
        val response = rest.getForEntity("/v1/prices?currency=USD", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(1, prices.size)
        assertEquals(101L, prices[0].id)
    }

    @Test
    fun `by product`() {
        val response = rest.getForEntity("/v1/prices?product-id=100", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(3, prices.size)
        assertEquals(listOf(100L, 101L, 102L), prices.map { price -> price.id })
    }

    @Test
    fun `by active`() {
        val response = rest.getForEntity("/v1/prices?active=false", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(1, prices.size)
        assertEquals(listOf(102L), prices.map { price -> price.id })
    }

    @Test
    fun `by date`() {
        val response = rest.getForEntity("/v1/prices?date=2024-03-10", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(3, prices.size)
        assertEquals(listOf(101L, 102L, 110L), prices.map { price -> price.id })
    }

    @Test
    fun `by account-type`() {
        val response = rest.getForEntity("/v1/prices?account-type-id=111", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(1, prices.size)
        assertEquals(listOf(102L), prices.map { price -> price.id })
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/prices?id=101&id=102&id=110", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(3, prices.size)
        assertEquals(listOf(101L, 102L, 110L), prices.map { price -> price.id })
    }

    @Test
    fun `another tenant`() {
        val response = rest.getForEntity("/v1/prices?id=200", SearchPriceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val prices = response.body!!.prices
        assertEquals(0, prices.size)
    }
}
