package com.wutsi.koki.platform.ai.genai

import java.lang.RuntimeException

class GenAIException(val statusCode: Int, message: String?, cause: Throwable) : RuntimeException(message, cause)
