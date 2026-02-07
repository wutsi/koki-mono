package com.wutsi.koki.platform.core.image

interface ImageService {
    fun transform(url: String, transformation: Transformation? = null): String
}
