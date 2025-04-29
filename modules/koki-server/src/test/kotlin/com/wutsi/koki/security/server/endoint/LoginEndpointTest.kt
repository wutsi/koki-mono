package com.wutsi.koki.security.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.dto.LoginResponse
import com.wutsi.koki.security.server.service.AccessTokenService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/security/LoginEndpoint.sql"])
class LoginEndpointTest : TenantAwareEndpointTest() {
    @Autowired
    private lateinit var accessTokenService: AccessTokenService

    @Test
    fun portal() {
        val request = LoginRequest(
            username = "ray.sponsible",
            password = "secret",
            application = ApplicationName.PORTAL,
        )

        val result = rest.postForEntity("/v1/auth/login", request, LoginResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accessToken = result.body!!.accessToken
        val principal = accessTokenService.decode(accessToken)
        assertEquals(11L, principal.getUserId())
        assertEquals(TENANT_ID, principal.getTenantId())
        assertEquals(request.application, principal.getApplication())
        assertEquals("Ray Sponsible", principal.getSubject())
        assertEquals("USER", principal.getSubjectType())
    }

    @Test
    fun client() {
        val request = LoginRequest(
            username = "woo.llc",
            password = "secret",
            application = ApplicationName.CLIENT,
        )

        val result = rest.postForEntity("/v1/auth/login", request, LoginResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accessToken = result.body!!.accessToken
        val principal = accessTokenService.decode(accessToken)
        assertEquals(100L, principal.getUserId())
        assertEquals(TENANT_ID, principal.getTenantId())
        assertEquals(request.application, principal.getApplication())
        assertEquals("WOO LLC", principal.getSubject())
        assertEquals("ACCOUNT", principal.getSubjectType())
    }
}
