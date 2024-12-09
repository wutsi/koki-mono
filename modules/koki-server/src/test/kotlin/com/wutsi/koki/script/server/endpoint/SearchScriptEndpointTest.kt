package com.wutsi.koki.script.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.script.dto.SearchScriptResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql

@Sql(value = ["/db/test/clean.sql", "/db/test/script/SearchScriptEndpoint.sql"])
class SearchScriptEndpointTest : TenantAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/scripts", SearchScriptResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val scripts = response.body!!.scripts
        assertEquals(4, scripts.size)
    }

    @Test
    fun `by id`() {
        val response =
            rest.getForEntity(
                "/v1/scripts?id=100&id=110&id=120&sort-by=NAME&asc=false",
                SearchScriptResponse::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val scripts = response.body!!.scripts
        assertEquals(3, scripts.size)
        assertEquals("120", scripts[0].id)
        assertEquals("110", scripts[1].id)
        assertEquals("100", scripts[2].id)
    }

    @Test
    fun `by name`() {
        val response =
            rest.getForEntity(
                "/v1/scripts?name=S-100&name=S-110&name=S-120&name=S-130&sort-by=TITLE",
                SearchScriptResponse::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val scripts = response.body!!.scripts
        assertEquals(4, scripts.size)
        assertEquals("100", scripts[0].id)
        assertEquals("130", scripts[1].id)
        assertEquals("110", scripts[2].id)
        assertEquals("120", scripts[3].id)
    }

    @Test
    fun `by active`() {
        val response =
            rest.getForEntity(
                "/v1/scripts?active=false&sort-by=CREATED_AT",
                SearchScriptResponse::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val scripts = response.body!!.scripts
        assertEquals(1, scripts.size)
        assertEquals("110", scripts[0].id)
    }

    @Test
    fun `from another tenant`() {
        val response =
            rest.getForEntity(
                "/v1/scripts?id=200&sort-by=MODIFIED_AT",
                SearchScriptResponse::class.java
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val scripts = response.body!!.scripts
        assertEquals(0, scripts.size)
    }
}
