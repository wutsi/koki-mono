package com.wutsi.koki.portal.service.storage.local

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import kotlin.test.Test


class LocalStorageServletTest {
    private val directory = System.getProperty("user.home") + "/__wutsi/koki"
    private val baseUrl = "http://localhost:8081/local-storage"
    private val storage = LocalStorageService(directory, baseUrl)

    private val request = mock<HttpServletRequest>()
    private val response = mock<HttpServletResponse>()
    private val servletOutput = mock<ServletOutputStream>()
    private val servlet = LocalStorageServlet(directory)

    @Test
    fun store() {
        // GIVEN
        val path = "2025/hello.txt"
        val content = "Hello world"
        store(path, content.toByteArray(), "text/plain")

        doReturn("/$path").whenever(request).pathInfo
        doReturn("GET").whenever(request).method
        doReturn(servletOutput).whenever(response).outputStream

        // WHEN
        servlet.service(request, response)

        // THEN
        verify(response).setContentType("text/plain")
        verify(response).setContentLength(content.length)
        verify(servletOutput).write(any(), any(), any())
    }

    @Test
    fun unsupportedExtension() {
        // GIVEN
        val path = "2025/hello.xxx"
        val content = "Hello world"
        store(path, content.toByteArray(), null)

        doReturn("/$path").whenever(request).pathInfo
        doReturn("GET").whenever(request).method
        doReturn(servletOutput).whenever(response).outputStream

        // WHEN
        servlet.service(request, response)

        // THEN
        verify(response).setContentType("application/octet-stream")
        verify(response).setContentLength(content.length)
        verify(servletOutput).write(any(), any(), any())
    }

    private fun store(path: String, content: ByteArray, contentType: String?) {
        storage.store(
            path = path,
            content = ByteArrayInputStream(content),
            contentType = contentType,
            contentLength = content.size.toLong(),
        )
    }
}
