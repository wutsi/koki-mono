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
    fun `by workflow`() {
        val response = rest.getForEntity("/v1/files?workflow-instance-id=wi-100", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(2, files.size)
    }

    @Test
    fun `by form`() {
        val response = rest.getForEntity("/v1/files?form-id=f-100&form-id=f-110", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(3, files.size)
    }

    @Test
    fun `by id`() {
        val response = rest.getForEntity("/v1/files?id=100&id=101&id=103", SearchFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val files = response.body!!.files
        assertEquals(3, files.size)
    }
}
