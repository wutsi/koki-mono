package com.wutsi.koki.platform.tracking

import com.wutsi.koki.track.dto.ChannelType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

interface ChannelTypeProvider {
    fun get(request: HttpServletRequest): ChannelType
    fun set(type: ChannelType, request: HttpServletRequest, response: HttpServletResponse)
}
