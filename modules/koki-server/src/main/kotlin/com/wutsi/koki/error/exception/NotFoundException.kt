package com.wutsi.koki.error.exception

import com.wutsi.koki.error.dto.Error
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException(error: Error, ex: Throwable? = null) : WutsiException(error, ex)
