package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.file.dto.GetFileResponse
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/file/GetFileEndpoint.sql"])
class GetFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Test
    fun get() {
        val response = rest.getForEntity("/v1/files/100", GetFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val file = response.body!!.file
        assertEquals("foo.pdf", file.name)
        assertEquals("https://www.file.com/foo.pdf", file.url)
        assertEquals("application/pdf", file.contentType)
        assertEquals(1000L, file.contentLength)
        assertEquals("wi-100", file.workflowInstanceId)
        assertEquals("f-100", file.formId)
        assertEquals(USER_ID, file.createdById)
    }

    @Test
    fun `not found`() {
        val response = rest.getForEntity("/v1/files/999", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.FILE_NOT_FOUND, response.body!!.error.code)
    }

    @Test
    fun `another tenant`() {
        val response = rest.getForEntity("/v1/files/200", ErrorResponse::class.java)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(ErrorCode.FILE_NOT_FOUND, response.body!!.error.code)
    }
}