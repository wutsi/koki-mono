package com.wutsi.koki.platform.debug

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class DebugRestInterceptor : ClientHttpRequestInterceptor {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DebugRestInterceptor::class.java)
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val response: ClientHttpResponse
        val timeInMillis = measureTimeMillis {
            response = execution.execute(request, body)
        }
        LOGGER.info("${request.method} ${request.uri} [${response.statusCode}] ${timeInMillis}ms")
        return response
    }
}
