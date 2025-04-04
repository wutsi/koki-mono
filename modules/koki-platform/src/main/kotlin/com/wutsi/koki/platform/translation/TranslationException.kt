package com.wutsi.koki.platform.translation

import java.lang.RuntimeException

open class TranslationException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
