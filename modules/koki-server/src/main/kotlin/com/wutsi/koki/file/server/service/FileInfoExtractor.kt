package com.wutsi.koki.file.server.service

import java.io.File
import java.io.IOException
import kotlin.jvm.Throws

interface FileInfoExtractor {
    @Throws(IOException::class)
    fun extract(file: File): FileInfo
}
