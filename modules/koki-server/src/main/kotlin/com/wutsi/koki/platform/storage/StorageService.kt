package com.wutsi.koki.platform.storage

import java.io.InputStream
import java.io.OutputStream
import java.net.URL

interface StorageService {
    fun store(
        path: String,
        content: InputStream,
        contentType: String?,
        contentLength: Long
    ): URL

    fun get(url: URL, os: OutputStream)
}
