package com.wutsi.koki.file.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.AuthorizationAwareEndpointTest
import com.wutsi.koki.common.dto.ObjectType
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.file.dto.FileStatus
import com.wutsi.koki.file.dto.FileType
import com.wutsi.koki.file.dto.GetFileResponse
import com.wutsi.koki.file.server.service.ImageResizer
import com.wutsi.koki.file.server.service.ImageResizerProvider
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Sql(value = ["/db/test/clean.sql", "/db/test/file/GetFileEndpoint.sql"])
class GetFileEndpointTest : AuthorizationAwareEndpointTest() {
    @MockitoBean
    private lateinit var resizerProvider: ImageResizerProvider

    @Test
    fun `get file`() {
        // WHEN
        val response = rest.getForEntity("/v1/files/100", GetFileResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val file = response.body!!.file
        assertEquals("foo.pdf", file.name)
        assertEquals("https://www.file.com/foo.pdf", file.url)
        assertEquals("application/pdf", file.contentType)
        assertEquals(1000L, file.contentLength)
        assertEquals(USER_ID, file.createdById)
        assertEquals(FileType.FILE, file.type)
        assertEquals(FileStatus.REJECTED, file.status)
        assertEquals("Invalid file", file.rejectionReason)
        assertEquals(null, file.owner)
        assertNull(file.tinyUrl)
        assertNull(file.thumbnailUrl)
        assertNull(file.previewUrl)
        assertNull(file.openGraphUrl)
    }

    @Test
    fun `get image - no resizer`() {
        // GIVEN
        doReturn(null).whenever(resizerProvider).get(any())

        // WHEN
        val response = rest.getForEntity("/v1/files/101", GetFileResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val file = response.body!!.file
        assertEquals("foo.png", file.name)
        assertEquals("https://www.file.com/foo.png", file.url)
        assertEquals("image/png", file.contentType)
        assertEquals(1000L, file.contentLength)
        assertEquals(USER_ID, file.createdById)
        assertEquals(FileType.IMAGE, file.type)
        assertEquals(FileStatus.REJECTED, file.status)
        assertEquals("Invalid file", file.rejectionReason)
        assertEquals(111L, file.owner?.id)
        assertEquals(ObjectType.ACCOUNT, file.owner?.type)
        assertNull(file.tinyUrl)
        assertNull(file.thumbnailUrl)
        assertNull(file.previewUrl)
        assertNull(file.openGraphUrl)
    }

    @Test
    fun `get image - with resizer`() {
        // GIVEN
        val resizer = mock<ImageResizer>()
        doReturn("https://www.resizer.com/tiny.png").whenever(resizer).tinyUrl(any())
        doReturn("https://www.resizer.com/thumbnail.png").whenever(resizer).thumbnailUrl(any())
        doReturn("https://www.resizer.com/preview.png").whenever(resizer).previewUrl(any())
        doReturn("https://www.resizer.com/og.png").whenever(resizer).openGraphUrl(any())

        doReturn(resizer).whenever(resizerProvider).get(any())

        // WHEN
        val response = rest.getForEntity("/v1/files/101", GetFileResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val file = response.body!!.file
        assertEquals("foo.png", file.name)
        assertEquals("https://www.file.com/foo.png", file.url)
        assertEquals("image/png", file.contentType)
        assertEquals(1000L, file.contentLength)
        assertEquals(USER_ID, file.createdById)
        assertEquals(FileType.IMAGE, file.type)
        assertEquals(FileStatus.REJECTED, file.status)
        assertEquals("Invalid file", file.rejectionReason)
        assertEquals(111L, file.owner?.id)
        assertEquals(ObjectType.ACCOUNT, file.owner?.type)
        assertEquals("https://www.resizer.com/tiny.png", file.tinyUrl)
        assertEquals("https://www.resizer.com/thumbnail.png", file.thumbnailUrl)
        assertEquals("https://www.resizer.com/preview.png", file.previewUrl)
        assertEquals("https://www.resizer.com/og.png", file.openGraphUrl)
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
