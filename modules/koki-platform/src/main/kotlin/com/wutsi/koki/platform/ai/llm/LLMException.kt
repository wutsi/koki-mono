package com.wutsi.koki.platform.ai.llm

import java.lang.RuntimeException

open class LLMException(val statusCode: Int, message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
