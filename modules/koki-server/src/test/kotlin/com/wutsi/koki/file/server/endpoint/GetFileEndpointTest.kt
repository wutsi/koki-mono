package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.CreateFileResponse
import com.wutsi.koki.file.server.dao.FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/file/CreateFileEndpoint.sql"])
class CreateFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FileRepository

    @Test
    fun create() {
        val request = CreateFileRequest(
            name = "foo.pdf",
            url = "https://www.files.com/foo.pdf",
            contentLength = 1024 * 1024L,
            contentType = "application/pdf",
            workflowInstanceId = UUID.randomUUID().toString()
        )
        val response = rest.postForEntity("/v1/files", request, CreateFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileId = response.body!!.fileId

        val file = dao.findById(fileId).get()
        assertEquals(request.name, file.name)
        assertEquals(request.url, file.url)
        assertEquals(request.contentType, file.contentType)
        assertEquals(request.contentLength, file.contentLength)
        assertEquals(request.workflowInstanceId, file.workflowInstanceId)
        assertEquals(USER_ID, file.createdById)
    }

    @Test
    fun anonymous() {
        anonymousUser = true
        val request = CreateFileRequest(
            name = "foo.pdf",
            url = "https://www.files.com/foo.pdf",
            contentLength = 1024 * 1024L,
            contentType = "application/pdf",
            workflowInstanceId = null
        )
        val response = rest.postForEntity("/v1/files", request, CreateFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileId = response.body!!.fileId

        val file = dao.findById(fileId).get()
        assertEquals(request.name, file.name)
        assertEquals(request.url, file.url)
        assertEquals(request.contentType, file.contentType)
        assertEquals(request.contentLength, file.contentLength)
        assertEquals(request.workflowInstanceId, file.workflowInstanceId)
        assertEquals(null, file.createdById)
    }
}
