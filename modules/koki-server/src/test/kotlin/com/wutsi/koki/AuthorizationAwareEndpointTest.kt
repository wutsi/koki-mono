package com.wutsi.koki

import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse

abstract class AuthorizationAwareEndpointTest : TenantAwareEndpointTest() {
    companion object {
        const val USER_ID = 11L
    }

    @Autowired
    private lateinit var authenticationService: AuthenticationService

    protected var anonymousUser: Boolean = false

    @BeforeEach
    override fun setUp() {
        anonymousUser = false
        super.setUp()
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        if (!anonymousUser) {
            val accessToken = createAccessToken()
            request.headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        } else {
            request.headers.remove(HttpHeaders.AUTHORIZATION)
        }
        return super.intercept(request, body, execution)
    }

    private fun createAccessToken(): String {
        return authenticationService.createAccessToken(
            UserEntity(
                id = USER_ID,
                tenantId = TENANT_ID,
            )
        )
    }
}
