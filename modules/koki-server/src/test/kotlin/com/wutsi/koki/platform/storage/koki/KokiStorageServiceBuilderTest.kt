package com.wutsi.koki.platform.storage.koki

import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.platform.storage.s3.S3StorageService
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertTrue

class KokiStorageServiceBuilderTest {
    @Test
    fun local() {
        val builder = KokiStorageServiceBuilder(
            type = "local",
            directory = "/foo",
            baseUrl = "https://localhost:8080/file-storage",
            s3Bucket = "",
            s3AccessKey = "",
            s3SecretKey = "",
            s3Region = ""
        )

        val storage = builder.build()
        assertTrue(storage is LocalStorageService)
    }

    @Test
    fun s3() {
        val builder = KokiStorageServiceBuilder(
            type = "s3",
            directory = "",
            baseUrl = "",
            s3Bucket = "test",
            s3AccessKey = "ACK-00000",
            s3SecretKey = "SEC-11111",
            s3Region = "us-east-1"
        )

        val storage = builder.build()
        assertTrue(storage is S3StorageService)
    }

    @Test
    fun other() {
        val builder = KokiStorageServiceBuilder(
            type = "xxx",
            directory = "",
            baseUrl = "",
            s3Bucket = "test",
            s3AccessKey = "ACK-00000",
            s3SecretKey = "SEC-11111",
            s3Region = "us-east-1"
        )

        assertThrows<IllegalStateException> { builder.build() }
    }
}
