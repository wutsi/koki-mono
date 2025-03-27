package com.wutsi.koki.platform.storage.s3

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.tenant.dto.ConfigurationName

class S3StorageServiceBuilder {
    companion object {
        val CONFIG_NAMES = listOf(
            ConfigurationName.STORAGE_S3_BUCKET,
            ConfigurationName.STORAGE_S3_REGION,
            ConfigurationName.STORAGE_S3_ACCESS_KEY,
            ConfigurationName.STORAGE_S3_SECRET_KEY,
        )
    }

    private val s3Builder: S3Builder = S3Builder()

    fun build(config: Map<String, String>): StorageService {
        validate(config)

        val bucket = config.get(ConfigurationName.STORAGE_S3_BUCKET)!!
        val region = config.get(ConfigurationName.STORAGE_S3_REGION)!!
        val accessKey = config.get(ConfigurationName.STORAGE_S3_ACCESS_KEY)!!
        val secretKey = config.get(ConfigurationName.STORAGE_S3_SECRET_KEY)!!
        return S3StorageService(bucket, s3Builder.build(region, accessKey, secretKey))
    }

    private fun validate(config: Map<String, String>) {
        val missing = CONFIG_NAMES.filter { name -> config[name] == null }
        if (missing.isNotEmpty()) {
            throw IllegalStateException("S3 not configured. Missing config: $missing")
        }
    }
}
