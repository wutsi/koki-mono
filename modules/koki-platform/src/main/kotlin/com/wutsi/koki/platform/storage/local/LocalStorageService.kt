package com.wutsi.koki.platform.storage.local

import com.wutsi.koki.platform.storage.StorageService
import com.wutsi.koki.platform.storage.StorageVisitor
import org.springframework.util.ResourceUtils.toURL
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
        val xpath = encodePath(path)
        val file = File("$directory/$xpath")
        file.parentFile.mkdirs()

        FileOutputStream(file)
            .use { output ->
                content.copyTo(output, BUF_SIZE)
                return URL("$baseUrl/$xpath")
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

    override fun toURL(path: String): URL {
        return URL("$baseUrl/$path")
    }

    override fun visit(path: String, visitor: StorageVisitor) {
        val file = toFile(path)
        visit(file, visitor)
    }

    private fun visit(file: File, visitor: StorageVisitor) {
        if (file.isFile) {
            visitor.visit(toURL(file))
        } else {
            file.listFiles()?.forEach { visit(it, visitor) }
        }
    }

    private fun toFile(path: String) = File("$directory/$path")

    private fun toURL(file: File): URL {
        val path = file.absolutePath.substring(directory.length + 1)
        return toURL(path)
    }

    private fun encodePath(path: String): String {
        return path.replace(" ", "-")
    }
}
