package com.wutsi.koki.file.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.dao.FileOwnerRepository
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
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

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun upload() {
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
        assertEquals(USER_ID, file.createdById)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(null, event.firstValue.owner)
    }

    @Test
    fun `upload and link`() {
        val entity = createEntity()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&owner-id=111&owner-type=" + ObjectType.ACCOUNT.name,
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
        assertEquals(ObjectType.ACCOUNT, fileOwners[0].ownerType)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(111, event.firstValue.owner?.id)
        assertEquals(ObjectType.ACCOUNT, event.firstValue.owner?.type)
    }

    @Test
    fun `upload with access-token as parameter`() {
        anonymousUser = true
        val entity = createEntity()
        val accessToken = createAccessToken()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&access-token=$accessToken",
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

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(null, event.firstValue.owner)
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
        assertEquals(null, file.createdById)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(null, event.firstValue.owner)
    }

    private fun createEntity(): HttpEntity<LinkedMultiValueMap<String, Any>> {
        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource("file.txt"))

        val headers = HttpHeaders()
        headers.setContentType(MediaType.MULTIPART_FORM_DATA)

        return HttpEntity<LinkedMultiValueMap<String, Any>>(parameters, headers)
    }
}
