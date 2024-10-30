package com.wutsi.koki.common.service

import com.wutsi.koki.common.dto.ErrorCode
import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.BadRequestException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service

@Service
class TenantIdProvider(private val request: HttpServletRequest) {
    fun get(): Long =
        try {
            request.getHeader(HttpHeader.TENANT_ID).toLong()
        } catch (ex: Exception) {
            throw BadRequestException(
                error = Error(
                    code = ErrorCode.TENANT_MISSING_FROM_HEADER
                )
            )
        }
}
