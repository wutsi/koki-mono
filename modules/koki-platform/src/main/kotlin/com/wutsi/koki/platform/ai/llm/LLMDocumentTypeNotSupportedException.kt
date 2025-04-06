package com.wutsi.koki.platform.ai.llm

import org.springframework.http.MediaType

class LLMDocumentTypeNotSupportedException(contentType: MediaType) : LLMException(-1, contentType.toString())
