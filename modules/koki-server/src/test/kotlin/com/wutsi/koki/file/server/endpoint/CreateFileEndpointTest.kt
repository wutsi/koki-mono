package com.wutsi.koki.file.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.UploadFileResponse
import com.wutsi.koki.file.dto.event.FileUploadedEvent
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
        assertEquals(FileType.FILE, file.type)
        assertEquals(null, file.ownerId)
        assertEquals(null, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

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
        assertEquals(111L, file.ownerId)
        assertEquals(ObjectType.ACCOUNT, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

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
        assertEquals(null, file.ownerId)
        assertEquals(null, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

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
        assertEquals(null, file.ownerId)
        assertEquals(null, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(null, event.firstValue.owner)
    }

    @Test
    fun `upload image`() {
        val entity = createEntity("fs/file/document.jpg")
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&type=IMAGE",
            HttpMethod.POST,
            entity,
            UploadFileResponse::class.java,
            "",
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf("*"), response.headers.get("Access-Control-Allow-Origin"))

        val fileId = response.body!!.id
        val file = dao.findById(fileId).get()
        assertEquals("document.jpg", file.name)
        assertEquals("image/jpeg", file.contentType)
        assertEquals(242755, file.contentLength)
        assertEquals(USER_ID, file.createdById)
        assertEquals(FileType.IMAGE, file.type)
        assertEquals(null, file.ownerId)
        assertEquals(null, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(null, event.firstValue.owner)
    }

    @Test
    fun `upload invalid image`() {
        val entity = createEntity()
        val response = rest.exchange(
            "/v1/files/upload?tenant-id=1&type=IMAGE",
            HttpMethod.POST,
            entity,
            ErrorResponse::class.java,
            "",
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(ErrorCode.FILE_NOT_IMAGE, response.body?.error?.code)
    }

    private fun createEntity(file: String = "file.txt"): HttpEntity<LinkedMultiValueMap<String, Any>> {
        val parameters = LinkedMultiValueMap<String, Any>()
        parameters.add("file", ClassPathResource(file))

        val headers = HttpHeaders()
        headers.setContentType(MediaType.MULTIPART_FORM_DATA)

        return HttpEntity<LinkedMultiValueMap<String, Any>>(parameters, headers)
    }
}
