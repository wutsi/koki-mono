package com.wutsi.koki.platform.storage.local

import com.wutsi.koki.platform.storage.StorageVisitor
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

    @Test
    fun visitor() {
        val content = ByteArrayInputStream("hello".toByteArray())
        storage.store("file.txt", content, "text/plain", 11L)
        storage.store("a/file-a1.txt", content, "text/plain", 11L)
        storage.store("a/file-a2.txt", content, "text/plain", 11L)
        storage.store("a/b/file-ab1.txt", content, "text/plain", 11L)
        storage.store("a/b/c/file-abc1.txt", content, "text/plain", 11L)

        val urls = mutableListOf<URL>()
        val visitor = object : StorageVisitor {
            override fun visit(url: URL) {
                urls.add(url)
            }
        }
        storage.visit("a", visitor)

        assertEquals(4, urls.size)
        assertTrue(urls.contains(URL("$baseUrl/a/file-a1.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/file-a2.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/b/file-ab1.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/b/c/file-abc1.txt")))
    }
}
