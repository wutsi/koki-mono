package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.tax.dto.SearchTaxResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/SearchTaxEndpoint.sql"])
class SearchTaxEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val result = rest.getForEntity("/v1/taxes", SearchTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(5, taxes.size)
    }

    @Test
    fun `by account`() {
        val result = rest.getForEntity("/v1/taxes?account-id=111", SearchTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(2, taxes.size)
        assertEquals(101L, taxes[0].id)
        assertEquals(100L, taxes[1].id)
    }

    @Test
    fun `by participant`() {
        val result = rest.getForEntity("/v1/taxes?participant-id=11", SearchTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(3, taxes.size)
        assertEquals(111L, taxes[0].id)
        assertEquals(101L, taxes[1].id)
        assertEquals(100L, taxes[2].id)
    }

    @Test
    fun `by assinee`() {
        val result = rest.getForEntity("/v1/taxes?participant-id=110", SearchTaxResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(2, taxes.size)
        assertEquals(102L, taxes[0].id)
        assertEquals(100L, taxes[1].id)
    }

    @Test
    fun `by status`() {
        val result = rest.getForEntity(
            "/v1/taxes?status=PREPARING&status=FINALIZING",
            SearchTaxResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(3, taxes.size)
        assertEquals(111L, taxes[0].id)
        assertEquals(110L, taxes[1].id)
        assertEquals(100L, taxes[2].id)
    }

    @Test
    fun `by creator`() {
        val result = rest.getForEntity(
            "/v1/taxes?created-by-id=55",
            SearchTaxResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(4, taxes.size)
    }

    @Test
    fun `by tax-type`() {
        val result = rest.getForEntity(
            "/v1/taxes?tax-type-id=100",
            SearchTaxResponse::class.java
        )

        assertEquals(HttpStatus.OK, result.statusCode)

        val taxes = result.body!!.taxes
        assertEquals(3, taxes.size)
        assertEquals(101L, taxes[0].id)
        assertEquals(102L, taxes[1].id)
        assertEquals(100L, taxes[2].id)
    }
}
