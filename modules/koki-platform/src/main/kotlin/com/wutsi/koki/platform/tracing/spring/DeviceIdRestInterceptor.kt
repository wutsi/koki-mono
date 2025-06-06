package com.wutsi.koki.platform.tracing.spring

import com.wutsi.koki.common.dto.HttpHeader
import com.wutsi.koki.platform.tracing.DeviceIdProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class DeviceIdRestInterceptor(
    private val deviceIdProvider: DeviceIdProvider,
    private val req: HttpServletRequest,
) : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        deviceIdProvider.get(req)?.let { id ->
            request.headers.add(HttpHeader.DEVICE_ID, id)
        }
        return execution.execute(request, body)
    }
}
