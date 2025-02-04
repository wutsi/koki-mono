package com.wutsi.koki.platform.storage.s3

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
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

    fun build(config: Map<String, String>): StorageService {
        validate(config)

        val bucket = config.get(ConfigurationName.STORAGE_S3_BUCKET)!!
        val region = config.get(ConfigurationName.STORAGE_S3_REGION)!!
        val accessKey = config.get(ConfigurationName.STORAGE_S3_ACCESS_KEY)!!
        val secretKey = config.get(ConfigurationName.STORAGE_S3_SECRET_KEY)!!
        return S3StorageService(bucket, s3(region, accessKey, secretKey))
    }

    private fun validate(config: Map<String, String>) {
        val missing = CONFIG_NAMES.filter { name -> config[name] == null }
        if (missing.isNotEmpty()) {
            throw IllegalStateException("S3 not configured. Missing config: $missing")
        }
    }

    private fun s3(region: String, accessKey: String, secretKey: String): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(region)
            .build()
    }
}
