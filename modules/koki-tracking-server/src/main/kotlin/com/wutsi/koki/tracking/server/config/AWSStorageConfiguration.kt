package com.wutsi.koki.tracking.server.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.s3.S3StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    value = ["koki.storage.type"],
    havingValue = "s3",
)
class AWSStorageConfiguration(
    @Value("\${koki.storage.s3.bucket}") private val bucket: String,
    @Value("\${koki.storage.s3.region}") private val region: String,
    @Value("\${koki.storage.s3.access-key}") private val accessKey: String,
    @Value("\${koki.storage.s3.secret-key}") private val secretKey: String,
) {
    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(region)
            .build()
    }

    @Bean
    fun storageService(): StorageService {
        return S3StorageService(bucket, amazonS3())
    }
}
