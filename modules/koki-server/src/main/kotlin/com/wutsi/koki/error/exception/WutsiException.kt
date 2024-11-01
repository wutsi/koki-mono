package com.wutsi.koki.error.exception

import com.wutsi.koki.error.dto.Error

open class WutsiException(val error: Error, cause: Throwable? = null) : RuntimeException(null, cause) {
    override val message: String?
        get() = "error-code=${error.code}"
}
