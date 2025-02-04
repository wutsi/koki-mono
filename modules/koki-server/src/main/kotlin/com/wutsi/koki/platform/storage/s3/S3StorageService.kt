package com.wutsi.koki.platform.storage.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.wutsi.koki.platform.storage.StorageService
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class S3StorageService(
    private val bucket: String,
    private val s3: AmazonS3,
) : StorageService {
    override fun store(
        path: String,
        content: InputStream,
        contentType: String?,
        contentLength: Long
    ): URL {
        val meta = ObjectMetadata()
        meta.contentLength = contentLength
        contentType?.let { meta.contentType = it }

        val request = PutObjectRequest(bucket, path, content, meta)
        try {
            s3.putObject(request)
            return URL(getUrlPrefix() + "/$path")
        } catch (e: Exception) {
            throw IOException(String.format("Unable to store to s3://%s/%s", bucket, path), e)
        }
    }

    override fun get(url: URL, os: OutputStream) {
        val path = url.path.substring(bucket.length + 2)
        val request = GetObjectRequest(bucket, path)
        try {
            val obj = s3.getObject(request)
            obj.use {
                obj.objectContent.copyTo(os)
            }
        } catch (e: Exception) {
            throw IOException(String.format("Unable to get s3://%s/%s", bucket, path), e)
        }
    }

    private fun getUrlPrefix(): String {
        return "https://s3.amazonaws.com/$bucket"
    }
}
