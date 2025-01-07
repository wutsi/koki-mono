package com.wutsi.koki.file.server.endpoint

import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.file.server.dao.FileOwnerRepository
import com.wutsi.koki.file.server.dao.FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.util.LinkedMultiValueMap
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/file/UploadFileEndpoint.sql"])
class UploadFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FileRepository

    @Autowired
    private lateinit var ownerDao: FileOwnerRepository

    @Test
    fun upload() {
        val entity = createEntity()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&workflow-instance-id=111&form-id=222",
            HttpMethod.POST,
            entity,
            UploadFileResponse::class.java,
            "",
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf("*"), response.headers.get("Access-Control-Allow-Origin"))

        val fileId = response.body!!.id
        val file = dao.findById(fileId).get()
        assertEquals("file.txt", file.name)
        assertEquals("text/plain", file.contentType)
        assertEquals(12, file.contentLength)
        assertEquals("111", file.workflowInstanceId)
        assertEquals("222", file.formId)
        assertEquals(USER_ID, file.createdById)
    }

    @Test
    fun `upload and link`() {
        val entity = createEntity()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&owner-id=111&owner-type=account",
            HttpMethod.POST,
            entity,
            UploadFileResponse::class.java,
            "",
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf("*"), response.headers.get("Access-Control-Allow-Origin"))

        val fileId = response.body!!.id
        val file = dao.findById(fileId).get()
        assertEquals("file.txt", file.name)
        assertEquals("text/plain", file.contentType)
        assertEquals(12, file.contentLength)
        assertEquals(USER_ID, file.createdById)

        val fileOwners = ownerDao.findByFileId(fileId)
        assertEquals(1, fileOwners.size)
        assertEquals(111L, fileOwners[0].ownerId)
        assertEquals("ACCOUNT", fileOwners[0].ownerType)
    }

    @Test
    fun `upload with access-token as parameter`() {
        anonymousUser = true
        val entity = createEntity()
        val accessToken = createAccessToken()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&workflow-instance-id=111&form-id=222&access-token=$accessToken",
            HttpMethod.POST,
            entity,
            UploadFileResponse::class.java,
            "",
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf("*"), response.headers.get("Access-Control-Allow-Origin"))

        val fileId = response.body!!.id
        val file = dao.findById(fileId).get()
        assertEquals("file.txt", file.name)
        assertEquals("text/plain", file.contentType)
        assertEquals(12, file.contentLength)
        assertEquals("111", file.workflowInstanceId)
        assertEquals("222", file.formId)
        assertEquals(USER_ID, file.createdById)
    }

    @Test
    fun anonymous() {
        anonymousUser = true
        val entity = createEntity()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1",
            HttpMethod.POST,
            entity,
            UploadFileResponse::class.java,
            "",
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf("*"), response.headers.get("Access-Control-Allow-Origin"))

        val fileId = response.body!!.id

        val file = dao.findById(fileId).get()
        assertEquals("file.txt", file.name)
        assertEquals("text/plain", file.contentType)
        assertEquals(12, file.contentLength)
        assertEquals(null, file.workflowInstanceId)
        assertEquals(null, file.formId)
        assertEquals(null, file.createdById)
    }

    private fun createEntity(): HttpEntity<LinkedMultiValueMap<String, Any>> {
        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource("file.txt"))

        val headers = HttpHeaders()
        headers.setContentType(MediaType.MULTIPART_FORM_DATA)

        return HttpEntity<LinkedMultiValueMap<String, Any>>(parameters, headers)
    }
}
