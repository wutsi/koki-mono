package com.wutsi.koki.service.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.service.dto.SearchServiceResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/service/SearchServiceEndpoint.sql"])
class SearchServiceEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/services", SearchServiceResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val services = response.body!!.services
        assertEquals(4, services.size)
    }

    @Test
    fun `by names`() {
        val response = rest.getForEntity(
            "/v1/services?name=SRV-100&name=SRV-110&name=SRV-120&sort-by=NAME&asc=true",
            SearchServiceResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val services = response.body!!.services
        assertEquals(3, services.size)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity(
            "/v1/services?id=100&id=110&id=120&sort-by=CREATED_AT",
            SearchServiceResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val services = response.body!!.services
        assertEquals(3, services.size)
    }

    @Test
    fun active() {
        val response = rest.getForEntity(
            "/v1/services?active=false&sort-by=MODIFIED_AT",
            SearchServiceResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val services = response.body!!.services
        assertEquals(1, services.size)
    }
}
