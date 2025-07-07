package com.wutsi.koki.platform.storage.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.platform.storage.StorageVisitor
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class S3StorageServiceTest {
    private val s3 = mock<AmazonS3>()
    private val bucket = "bucket"
    private val storage = S3StorageService(bucket, s3)

    @Test
    fun store() {
        val content = ByteArrayInputStream("hello".toByteArray())
        val result = storage.store("document/test.txt", content, "text/plain", 10000L)

        assertNotNull(result)
        assertEquals(URL("https://s3.amazonaws.com/bucket/document/test.txt"), result)

        val request: ArgumentCaptor<PutObjectRequest> = ArgumentCaptor.forClass(PutObjectRequest::class.java)
        verify(s3).putObject(request.capture())
        assertEquals(request.value.bucketName, bucket)
        assertEquals(request.value.metadata.contentType, "text/plain")
        assertEquals(request.value.metadata.contentLength, 10000L)
    }

    @Test
    fun `file with space`() {
        val content = ByteArrayInputStream("hello".toByteArray())
        val result = storage.store("document/yo man.txt", content, "text/plain", 10000L)

        assertNotNull(result)
        assertEquals(URL("https://s3.amazonaws.com/bucket/document/yo-man.txt"), result)

        val request: ArgumentCaptor<PutObjectRequest> = ArgumentCaptor.forClass(PutObjectRequest::class.java)
        verify(s3).putObject(request.capture())
        assertEquals(request.value.bucketName, bucket)
        assertEquals(request.value.metadata.contentType, "text/plain")
        assertEquals(request.value.metadata.contentLength, 10000L)
    }

    @Test
    fun `file with accent`() {
        val content = ByteArrayInputStream("hello".toByteArray())
        val result = storage.store("document/éâêîôû.txt", content, "text/plain", 10000L)

        assertNotNull(result)
        assertEquals(URL("https://s3.amazonaws.com/bucket/document/eaeiou.txt"), result)

        val request: ArgumentCaptor<PutObjectRequest> = ArgumentCaptor.forClass(PutObjectRequest::class.java)
        verify(s3).putObject(request.capture())
        assertEquals(request.value.bucketName, bucket)
        assertEquals(request.value.metadata.contentType, "text/plain")
        assertEquals(request.value.metadata.contentLength, 10000L)
    }

    @Test
    fun storeWithError() {
        doThrow(RuntimeException::class).whenever(s3).putObject(ArgumentMatchers.any())

        val content = ByteArrayInputStream("hello".toByteArray())
        assertThrows<IOException> {
            storage.store("document/test.txt", content, "text/plain", 10000L)
        }
    }

    @Test
    fun get() {
        val url = "https://s3.amazonaws.com/$bucket/100/document/203920392/toto.txt"
        val os = ByteArrayOutputStream()

        val obj: S3Object = mock()
        val content: S3ObjectInputStream = mock()
        doReturn(-1).whenever(content).read(ArgumentMatchers.any())
        doReturn(content).whenever(obj).objectContent
        doReturn(obj).whenever(s3).getObject(ArgumentMatchers.any())

        storage.get(URL(url), os)

        val request: ArgumentCaptor<GetObjectRequest> = ArgumentCaptor.forClass(GetObjectRequest::class.java)
        verify(s3).getObject(request.capture())
        assertEquals(request.value.bucketName, bucket)
        assertEquals(request.value.key, "100/document/203920392/toto.txt")
    }

    @Test
    fun `get - error`() {
        doThrow(IllegalStateException::class).whenever(s3).getObject(any())

        val url = "https://s3.amazonaws.com/$bucket/100/document/203920392/toto.txt"
        val os = ByteArrayOutputStream()

        assertThrows<IOException> { storage.get(URL(url), os) }
    }

    @Test
    fun visit() {
        val listings = Mockito.mock(ObjectListing::class.java)
        doReturn(
            listOf(
                createObjectSummary("a/file-a1.txt"),
                createObjectSummary("a/file-a2.txt"),
                createObjectSummary("a/b/file-ab1.txt"),
                createObjectSummary("a/b/c/file-abc1.txt"),
            ),
        ).whenever(listings).objectSummaries
        doReturn(listings).whenever(s3).listObjects(ArgumentMatchers.any(ListObjectsRequest::class.java))

        val urls = mutableListOf<URL>()
        val visitor = createStorageVisitor(urls)
        val baseUrl = "https://s3.amazonaws.com/bucket"

        storage.visit("a", visitor)
        assertEquals(4, urls.size)
        assertTrue(urls.contains(URL("$baseUrl/a/file-a1.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/file-a2.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/b/file-ab1.txt")))
        assertTrue(urls.contains(URL("$baseUrl/a/b/c/file-abc1.txt")))
    }

    private fun createStorageVisitor(urls: MutableList<URL>) = object : StorageVisitor {
        override fun visit(url: URL) {
            urls.add(url)
        }
    }

    private fun createObjectSummary(key: String): S3ObjectSummary {
        val obj = S3ObjectSummary()
        obj.key = key
        return obj
    }
}
