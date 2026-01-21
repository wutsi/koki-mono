package com.wutsi.koki.file.server.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectReference
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.file.dto.CreateFileRequest
import com.wutsi.koki.file.dto.CreateFileResponse
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.event.FileUploadedEvent
import com.wutsi.koki.file.server.dao.FileRepository
import com.wutsi.koki.platform.mq.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals

@Sql(value = ["/db/test/clean.sql", "/db/test/file/CreateFileEndpoint.sql"])
class CreateFileEndpointTest : AuthorizationAwareEndpointTest() {
    @Autowired
    private lateinit var dao: FileRepository

    @MockitoBean
    private lateinit var publisher: Publisher

    @Test
    fun image() {
        val request = CreateFileRequest(
            url = "https://picsum.photos/200/300",
            owner = ObjectReference(
                id = 555L,
                type = ObjectType.LISTING
            )
        )
        val response = rest.postForEntity("/v1/files", request, CreateFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileId = response.body!!.fileId
        val file = dao.findById(fileId).get()
        assertEquals(true, file.name.endsWith(".jpg"))
        assertEquals("image/jpeg", file.contentType)
        assertEquals(true, file.contentLength > 500)
        assertEquals(USER_ID, file.createdById)
        assertEquals(FileType.IMAGE, file.type)
        assertEquals(request.owner?.id, file.ownerId)
        assertEquals(request.owner?.type, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(request.owner?.id, event.firstValue.owner?.id)
        assertEquals(request.owner?.type, event.firstValue.owner?.type)
    }

    @Test
    fun pdf() {
        val request = CreateFileRequest(
            url = "https://www.cte.iup.edu/cte/Resources/PDF_TestPage.pdf",
            owner = null
        )
        val response = rest.postForEntity("/v1/files", request, CreateFileResponse::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val fileId = response.body!!.fileId
        val file = dao.findById(fileId).get()
        assertEquals(true, file.name.endsWith(".pdf"))
        assertEquals("application/pdf", file.contentType)
        assertEquals(true, file.contentLength > 500)
        assertEquals(USER_ID, file.createdById)
        assertEquals(FileType.FILE, file.type)
        assertEquals(request.owner?.id, file.ownerId)
        assertEquals(request.owner?.type, file.ownerType)
        assertEquals(FileStatus.UNDER_REVIEW, file.status)

        val event = argumentCaptor<FileUploadedEvent>()
        verify(publisher).publish(event.capture())
        assertEquals(file.id, event.firstValue.fileId)
        assertEquals(file.tenantId, event.firstValue.tenantId)
        assertEquals(request.owner?.id, event.firstValue.owner?.id)
        assertEquals(request.owner?.type, event.firstValue.owner?.type)
    }
}
