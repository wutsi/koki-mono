package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tax.dto.SearchTaxTypeResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/SearchTaxTypeEndpoint.sql"])
class SearchTaxTypeEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/tax-types", SearchTaxTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxs = result.body!!.taxTypes
        assertEquals(4, taxs.size)
    }

    @Test
    fun `by name`() {
        val result = rest.getForEntity("/v1/tax-types?name=T1&name=T2", SearchTaxTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxs = result.body!!.taxTypes
        assertEquals(2, taxs.size)
        assertEquals(100L, taxs[0].id)
        assertEquals(101L, taxs[1].id)
    }

    @Test
    fun `by id`() {
        val result =
            rest.getForEntity("/v1/tax-types?id=103&id=100&id=101", SearchTaxTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxs = result.body!!.taxTypes
        assertEquals(3, taxs.size)
        assertEquals(100L, taxs[0].id)
        assertEquals(101L, taxs[1].id)
        assertEquals(103L, taxs[2].id)
    }

    @Test
    fun active() {
        val result = rest.getForEntity("/v1/tax-types?active=false", SearchTaxTypeResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxs = result.body!!.taxTypes
        assertEquals(1, taxs.size)
        assertEquals(103L, taxs[0].id)
    }
}
