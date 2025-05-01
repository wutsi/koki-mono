package com.wutsi.koki

import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.server.service.AccessTokenService
import com.wutsi.koki.tenant.dto.UserType
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
    private lateinit var accessTokenService: AccessTokenService

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
        if (!this.anonymousUser) {
            val accessToken = createAccessToken()
            request.headers.add(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        } else {
            request.headers.remove(HttpHeaders.AUTHORIZATION)
        }
        return super.intercept(request, body, execution)
    }

    protected fun createAccessToken(application: String = ApplicationName.PORTAL): String {
        return accessTokenService.create(
            application = application,
            tenantId = TENANT_ID,
            userId = USER_ID,
            subject = "Ray Sponsible",
            subjectType = UserType.EMPLOYEE,
        )
    }
}
