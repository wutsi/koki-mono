package com.wutsi.koki.platform.storage.koki

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.local.LocalStorageService
import com.wutsi.koki.platform.storage.s3.S3Builder
import com.wutsi.koki.platform.storage.s3.S3StorageService

class KokiStorageServiceBuilder(
    private val type: String,
    private val directory: String,
    private val baseUrl: String,
    private val s3Bucket: String,
    private val s3AccessKey: String,
    private val s3SecretKey: String,
    private val s3Region: String,
) {
    private val s3Builder: S3Builder = S3Builder()

    fun build(): StorageService {
        if (type == "local") {
            return LocalStorageService(directory, baseUrl)
        } else if (type == "s3") {
            return S3StorageService(s3Bucket, s3Builder.build(s3Region, s3AccessKey, s3SecretKey))
        } else {
            throw IllegalStateException("Storage not supported: $type")
        }
    }
}
