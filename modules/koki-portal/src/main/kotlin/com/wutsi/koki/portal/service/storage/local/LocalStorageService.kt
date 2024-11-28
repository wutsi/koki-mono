package com.wutsi.koki.portal.service.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

@Service
class LocalStorageService(
    @Value("\${koki.storage.local.directory}") private val directory: String,
    @Value("\${koki.storage.local.base-url}") private val baseUrl: String,
) : StorageService {
    companion object {
        const val BUF_SIZE = 1024
    }

    override fun store(
        path: String,
        content: InputStream,
        contentType: String,
        contentLength: Long
    ): URL {
        val file = File("$directory/$path")
        file.parentFile.mkdirs()

        FileOutputStream(file)
            .use {
                content.copyTo(it, BUF_SIZE)
                return URL("$baseUrl/$path")
            }
    }
}
