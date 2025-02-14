package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.dto.SearchSalesTaxResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/SearchSalesTaxEndpoint.sql"])
class SearchSalesTaxEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by country`() {
        val response = rest.getForEntity("/v1/sales-taxes?country=CM", SearchSalesTaxResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.salesTaxes
        assertEquals(2, taxes.size)
    }

    @Test
    fun `by state`() {
        val response = rest.getForEntity("/v1/sales-taxes?country=CM&state-id=111", SearchSalesTaxResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.salesTaxes
        assertEquals(2, taxes.size)
    }

    @Test
    fun `by active`() {
        val response = rest.getForEntity("/v1/sales-taxes?active=false", SearchSalesTaxResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.salesTaxes
        assertEquals(1, taxes.size)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/sales-taxes?id=1011&id=1111&id=1012", SearchSalesTaxResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.salesTaxes
        assertEquals(2, taxes.size)
    }
}
