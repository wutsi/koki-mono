package com.wutsi.koki.platform.ai.llm

import org.springframework.http.MediaType
import java.io.InputStream

data class LLMDocument(
    val contentType: MediaType,
    val content: InputStream,
)
