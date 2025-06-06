package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.file.dto.SearchFileResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/file/SearchFileEndpoint.sql"])
class SearchFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun all() {
        val response = rest.getForEntity("/v1/files", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(5, files.size)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/files?id=100&id=101&id=103&id=199", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(3, files.size)
    }

    @Test
    fun `by owner`() {
        val response = rest.getForEntity("/v1/files?owner-id=11&owner-type=ACCOUNT", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(2, files.size)
        assertEquals(104L, files[0].id)
        assertEquals(100L, files[1].id)
    }

    @Test
    fun `by type`() {
        val response = rest.getForEntity("/v1/files?type=IMAGE", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(2, files.size)
        assertEquals(103L, files[0].id)
        assertEquals(102L, files[1].id)
    }

    @Test
    fun `by status`() {
        val response = rest.getForEntity("/v1/files?status=APPROVED", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(2, files.size)
        assertEquals(101L, files[0].id)
        assertEquals(103L, files[1].id)
    }
}
