package com.wutsi.koki.refdata.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.refdata.dto.SearchJuridictionResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/refdata/SearchJuridictionEndpoint.sql"])
class SearchJuridictionEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun `by country`() {
        val response = rest.getForEntity("/v1/juridictions?country=CM", SearchJuridictionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.juridictions
        assertEquals(1, taxes.size)
        assertEquals(2001L, taxes[0].id)
    }

    @Test
    fun `by state`() {
        val response =
            rest.getForEntity("/v1/juridictions?state-id=111", SearchJuridictionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.juridictions
        assertEquals(1, taxes.size)
    }

    @Test
    fun `by id`() {
        val response =
            rest.getForEntity("/v1/juridictions?id=1011&id=1091&id=1112", SearchJuridictionResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val taxes = response.body!!.juridictions
        assertEquals(3, taxes.size)
    }
}
