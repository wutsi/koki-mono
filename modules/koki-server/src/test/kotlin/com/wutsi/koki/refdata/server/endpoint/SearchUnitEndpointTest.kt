package com.wutsi.koki.product.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.dto.SearchUnitResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/SearchUnitEndpoint.sql"])
class SearchUnitEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/units", SearchUnitResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val units = response.body!!.units
        assertEquals(13, units.size)
    }
}
