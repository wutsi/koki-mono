package com.wutsi.koki.portal.service.storage.local

import java.io.ByteArrayInputStream
import java.io.File
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalStorageServiceTest {
    private val directory = System.getProperty("user.home") + "/wutsi/koki"
    private val baseUrl = "http://localhost:8081/storage"

    private val storage = LocalStorageService(directory, baseUrl)

    @Test
    fun upload() {
        val content = "Hello world"

        val url = storage.store(
            path = "2025/hello.txt",
            content = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8)),
            contentType = "text/plain",
            contentLength = content.length.toLong(),
        )

        assertEquals(URL("$baseUrl/2025/hello.txt"), url)

        val file = File("$directory/2025/hello.txt")
        assertTrue(file.exists())
        assertEquals(content, file.readText())
    }
}
