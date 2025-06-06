package com.wutsi.koki.platform.tracing.servlet

import com.wutsi.koki.platform.tracing.DeviceIdProvider
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class DeviceIdFilter(private val deviceIdProvider: DeviceIdProvider) : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            val value = deviceIdProvider.get(request as HttpServletRequest)
            if (value != null) {
                deviceIdProvider.set(value, request, response as HttpServletResponse)
            }
        } finally {
            chain.doFilter(request, response)
        }
    }
}
