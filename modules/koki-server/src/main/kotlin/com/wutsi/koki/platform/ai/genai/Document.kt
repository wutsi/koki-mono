package com.wutsi.koki.platform.ai.genai

import org.springframework.http.MediaType

data class Document(
    val contentType: MediaType,
    val content: ByteArray,
)
