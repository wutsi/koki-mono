package com.wutsi.koki.platform.ai.llm

import java.lang.RuntimeException

class LLMException(val statusCode: Int, message: String?, cause: Throwable?) : RuntimeException(message, cause)
