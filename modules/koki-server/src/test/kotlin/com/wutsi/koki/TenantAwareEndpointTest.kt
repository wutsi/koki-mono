package com.wutsi.koki.tenant.server.endpoint

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TenantAwareEndpointTest : ClientHttpRequestInterceptor {
    companion object {
        const val TENANT_ID = 1L
    }

    @Autowired
    protected lateinit var rest: TestRestTemplate

    protected open fun getTenantId() = TENANT_ID

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        request.headers.add("X-Tenant-ID", getTenantId().toString())
        return execution.execute(request, body)
    }

    @BeforeEach
    fun setUp() {
        rest.restTemplate.interceptors.add(this)
    }
}
