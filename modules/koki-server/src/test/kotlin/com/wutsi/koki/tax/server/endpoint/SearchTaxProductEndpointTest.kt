package com.wutsi.koki.tax.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.tax.dto.SearchTaxProductResponse
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/tax/SearchTaxProductEndpoint.sql"])
class SearchTaxProductEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun search() {
        val result = rest.getForEntity("/v1/tax-products?tax-id=100", SearchTaxProductResponse::class.java)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(3, result.body!!.taxProducts.size)
    }
}
