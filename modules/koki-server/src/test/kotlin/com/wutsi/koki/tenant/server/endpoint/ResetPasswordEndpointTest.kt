package com.wutsi.koki.tenant.server.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.TenantAwareEndpointTest
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.dto.ErrorResponse
import com.wutsi.koki.platform.mq.Publisher
import com.wutsi.koki.tenant.dto.ResetPasswordRequest
import com.wutsi.koki.tenant.server.dao.PasswordResetTokenRepository
import com.wutsi.koki.tenant.server.dao.UserRepository
import com.wutsi.koki.tenant.server.service.PasswordEncryptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.jdbc.Sql
import java.util.Date
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/test/clean.sql", "/db/test/tenant/ResetPasswordEndpoint.sql"])
class ResetPasswordEndpointTest : TenantAwareEndpointTest() {
    companion object {
        const val HASHED_PASSWORD = "607e0b9e5496964b1385b7c10e3e2403"
    }

    @MockitoBean
    private lateinit var publisher: Publisher

    @Autowired
    private lateinit var dao: PasswordResetTokenRepository

    @Autowired
    private lateinit var userDao: UserRepository

    @MockitoBean
    private lateinit var passwordEncryptor: PasswordEncryptor

    @BeforeEach
    override fun setUp() {
        super.setUp()
        doReturn(HASHED_PASSWORD).whenever(passwordEncryptor).hash(any(), any())
    }

    @Test
    fun send() {
        val now = Date()

        val request = ResetPasswordRequest(
            tokenId = "token-11",
            password = "Secret123"
        )
        val result = rest.postForEntity("/v1/users/password/reset", request, Any::class.java)

        assertEquals(HttpStatus.OK, result.statusCode)

        val tokenId = request.tokenId

        val token = dao.findById(tokenId).get()
        assertEquals(true, token.expiresAt.before(now))

        val salt = argumentCaptor<String>()
        verify(passwordEncryptor).hash(eq(request.password), salt.capture())

        val user = userDao.findById(token.user.id ?: -1).get()
        assertEquals(HASHED_PASSWORD, user.password)
        assertEquals(salt.firstValue, user.salt)
    }

    @Test
    fun `expired token`() {
        val request = ResetPasswordRequest(
            tokenId = "token-expired",
            password = "Secret123"
        )
        val result = rest.postForEntity("/v1/users/password/reset", request, ErrorResponse::class.java)

        verify(publisher, never()).publish(any())
        assertEquals(HttpStatus.CONFLICT, result.statusCode)

        assertEquals(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED, result.body?.error?.code)
    }

    @Test
    fun `token not found`() {
        val request = ResetPasswordRequest(
            tokenId = "xxx",
            password = "Secret123"
        )
        val result = rest.postForEntity("/v1/users/password/reset", request, ErrorResponse::class.java)

        verify(publisher, never()).publish(any())
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)

        assertEquals(ErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND, result.body?.error?.code)
    }
}
