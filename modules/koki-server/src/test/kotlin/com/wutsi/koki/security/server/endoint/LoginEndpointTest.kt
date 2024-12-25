package com.wutsi.koki.security.server.endpoint

import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.party.dto.LoginRequest
import com.wutsi.koki.party.dto.LoginResponse
import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.tenant.dto.CreateUserRequest
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
    private lateinit var authenticationService: AuthenticationService

    @Test
    fun login() {
        val request = LoginRequest(
            email = "ray.sponsible@gmail.com",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/auth/login", request, LoginResponse::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val accessToken = result.body!!.accessToken
        val principal = authenticationService.decodeAccessToken(accessToken)
        assertEquals(11L, principal.getUserId())
        assertEquals(getTenantId(), principal.getTenantId())
        assertEquals("Ray Sponsible", principal.getSubject())
    }

    @Test
    fun `invalid email`() {
        val request = CreateUserRequest(
            email = "OMAM.MBIYICK@hotmail.com",
            displayName = "Omam Mbiyick",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/auth/login", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, result.body?.error?.code)
    }

    @Test
    fun `invalid password`() {
        val request = LoginRequest(
            email = "ray.sponsible@gmail.com",
            password = "xxxx"
        )

        val result = rest.postForEntity("/v1/auth/login", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, result.body?.error?.code)
    }

    @Test
    fun `user of another tenant`() {
        val request = LoginRequest(
            email = "roger.milla@gmail.com",
            password = "secret"
        )

        val result = rest.postForEntity("/v1/auth/login", request, ErrorResponse::class.java)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, result.body?.error?.code)
    }
}
