package com.wutsi.koki.platform.tracing

import com.wutsi.koki.common.dto.HttpHeader
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class HeaderDeviceIdProvider : DeviceIdProvider {
    override fun get(request: HttpServletRequest): String? {
        return request.getHeader(HttpHeader.DEVICE_ID)
    }

    override fun set(id: String, request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader(HttpHeader.DEVICE_ID, id)
    }
}
