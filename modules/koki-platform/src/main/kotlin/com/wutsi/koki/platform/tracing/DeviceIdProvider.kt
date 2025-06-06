package com.wutsi.koki.platform.tracing

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface DeviceIdProvider {
    fun get(request: HttpServletRequest): String?
    fun set(id: String, request: HttpServletRequest, response: HttpServletResponse)
}
