package com.wutsi.koki.portal.file.service

import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.stereotype.Service

@Service
class S3Validator {
    @Throws(AmazonClientException::class)
    fun validate(bucket: String, region: String, accessKey: String, secretKey: String) {
        val s3 = AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(region)
            .build()

        s3.getBucketLocation(bucket)
    }
}
