package com.wutsi.koki.platform.storage.s3

import com.wutsi.koki.tenant.dto.ConfigurationName
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class S3StorageServiceBuilderTest {
    private val config = mapOf(
        ConfigurationName.STORAGE_S3_BUCKET to "test",
        ConfigurationName.STORAGE_S3_REGION to "us-east-1",
        ConfigurationName.STORAGE_S3_ACCESS_KEY to "access-key",
        ConfigurationName.STORAGE_S3_SECRET_KEY to "secret-key",
        ConfigurationName.SMTP_FROM_ADDRESS to "no-reply@koki.com",
        ConfigurationName.SMTP_FROM_PERSONAL to "Koki"
    )
    private val builder = S3StorageServiceBuilder()

    @Test
    fun build() {
        val storage = builder.build(config)
        assertEquals(true, storage is S3StorageService)
    }

    @Test
    fun `missing bucket`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.STORAGE_S3_BUCKET))
        }
    }

    @Test
    fun `missing region`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.STORAGE_S3_REGION))
        }
    }

    @Test
    fun `missing access-key`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.STORAGE_S3_ACCESS_KEY))
        }
    }

    @Test
    fun `missing secret-key`() {
        assertThrows<IllegalStateException> {
            builder.build(createConfigExcluding(ConfigurationName.STORAGE_S3_SECRET_KEY))
        }
    }

    private fun createConfigExcluding(name: String): Map<String, String> {
        return config.filter { entry -> entry.key != name }
    }
}
