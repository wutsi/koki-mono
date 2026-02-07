package com.wutsi.koki.file.server.service

interface ImageTransformer {
    fun tinyUrl(url: String): String
    fun thumbnailUrl(url: String): String
    fun previewUrl(url: String): String
}
