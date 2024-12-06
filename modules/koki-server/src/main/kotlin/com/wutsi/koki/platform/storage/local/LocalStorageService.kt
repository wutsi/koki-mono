package com.wutsi.koki.platform.storage.local

import com.wutsi.koki.platform.storage.StorageService
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import kotlin.io.copyTo
import kotlin.io.use

class LocalStorageService(
    private val directory: String,
    private val baseUrl: String,
) : StorageService {
    companion object {
        const val BUF_SIZE = 10024 // 10K
    }

    override fun store(
        path: String,
        content: InputStream,
        contentType: String?,
        contentLength: Long
    ): URL {
        val file = File("$directory/$path")
        file.parentFile.mkdirs()

        FileOutputStream(file)
            .use { output ->
                content.copyTo(output, BUF_SIZE)
                return URL("$baseUrl/$path")
            }
    }

    override fun get(url: URL, os: OutputStream) {
        val path = url.toString().substring(this.baseUrl.length)
        val file = File("$directory/$path")
        val fis = FileInputStream(file)
        fis.use {
            fis.copyTo(os, BUF_SIZE)
        }
    }
}
