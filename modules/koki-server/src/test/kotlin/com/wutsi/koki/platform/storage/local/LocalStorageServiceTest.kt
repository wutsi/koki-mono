package com.wutsi.koki.portal.service.storage.local

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalStorageServiceTest {
    private val directory = System.getProperty("user.home") + "/__wutsi/koki"
    private val baseUrl = "http://localhost:8081/local-storage"
    private val storage = LocalStorageService(directory, baseUrl)

    @Test
    fun store() {
        // WHEN
        val content = "Hello world"
        val url = storage.store(
            path = "2025/hello.txt",
            content = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8)),
            contentType = "text/plain",
            contentLength = content.length.toLong(),
        )

        // THEN
        assertEquals(URL("$baseUrl/2025/hello.txt"), url)

        val file = File("$directory/2025/hello.txt")
        assertTrue(file.exists())
        assertEquals(content, file.readText())
    }

    @Test
    fun get() {
        // GIVEN
        val content = "Yo Man"
        val url = storage.store(
            path = "2026/hello.txt",
            content = ByteArrayInputStream(content.toByteArray(Charsets.UTF_8)),
            contentType = "text/plain",
            contentLength = content.length.toLong(),
        )

        // WHEN
        val output = ByteArrayOutputStream()
        storage.get(url, output)

        // THEN
        assertEquals(content, String(output.toByteArray()))
    }
}
