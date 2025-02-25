package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.product.dto.SearchProductResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/product/SearchProductEndpoint.sql"])
class SearchProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/products", SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val products = response.body!!.products
        assertEquals(5, products.size)
    }

    @Test
    fun `by types`() {
        val response = rest.getForEntity("/v1/products?type=DIGITAL&type=PHYSICAL", SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val products = response.body!!.products
        assertEquals(4, products.size)
        assertEquals(listOf(110L, 120L, 130L, 140L), products.map { product -> product.id }.sorted())
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/products?id=120&id=140&id=200", SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val products = response.body!!.products
        assertEquals(2, products.size)
        assertEquals(listOf(120L, 140L), products.map { product -> product.id }.sorted())
    }

    @Test
    fun `by active`() {
        val response = rest.getForEntity("/v1/products?active=false", SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val products = response.body!!.products
        assertEquals(2, products.size)
        assertEquals(listOf(120L, 140L), products.map { product -> product.id }.sorted())
    }

    @Test
    fun `by keywords - name`() {
        val response = rest.getForEntity("/v1/products?q=PRO", SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val products = response.body!!.products
        assertEquals(2, products.size)
        assertEquals(listOf(110L, 120L), products.map { product -> product.id }.sorted())
    }

    @Test
    fun `by keywords - code`() {
        val response = rest.getForEntity("/v1/products?q=P-", SearchProductResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val products = response.body!!.products
        assertEquals(3, products.size)
        assertEquals(listOf(110L, 120L, 140L), products.map { product -> product.id }.sorted())
    }
}
