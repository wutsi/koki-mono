package com.wutsi.koki.tenant.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.server.service.AccessTokenService
import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.security.server.service.UserAuthenticator
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.domain.UserEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserAuthenticatorTest {
    private val authenticatorService = mock<AuthenticationService>()
    private val userService = mock<UserService>()
    private val passwordService = mock<PasswordService>()
    private val accessTokenService = mock<AccessTokenService>()
    private val authenticator = UserAuthenticator(
        authenticatorService = authenticatorService,
        userService = userService,
        passwordService = passwordService,
        accessTokenService = accessTokenService,
    )

    private val user = UserEntity(
        id = 111,
        tenantId = 222,
        displayName = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
        status = UserStatus.ACTIVE,
        salt = UUID.randomUUID().toString()
    )
    private val accessToken = UUID.randomUUID().toString()
    private val request = LoginRequest(
        username = user.email,
        password = user.password,
        application = ApplicationName.PORTAL,
    )

    @BeforeEach
    fun setup() {
        doReturn(user).whenever(userService).getByUsername(any(), any(), any())
        doReturn(accessToken).whenever(accessTokenService).create(any(), any(), any(), any(), any())
        doReturn(true).whenever(passwordService).matches(any(), any(), any())
    }

    @Test
    fun init() {
        authenticator.init()
        verify(authenticatorService).register(authenticator)
    }

    @Test
    fun destroy() {
        authenticator.destroy()
        verify(authenticatorService).unregister(authenticator)
    }

    @Test
    fun supports() {
        assertTrue(authenticator.supports(request))
        assertFalse(authenticator.supports(request.copy(application = ApplicationName.CLIENT)))
    }

    @Test
    fun authenticate() {
        val result = authenticator.authenticate(request, user.tenantId)
        assertEquals(accessToken, result)
        verify(accessTokenService).create(
            request.application,
            user.id ?: -1,
            user.displayName,
            UserType.EMPLOYEE,
            user.tenantId,
        )
    }

    @Test
    fun `user not found`() {
        doThrow(NotFoundException(Error())).whenever(userService).getByUsername(any(), any(), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.error.code)
    }

    @Test
    fun `password mismatch`() {
        doReturn(false).whenever(passwordService).matches(any(), any(), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.error.code)
    }

    @Test
    fun `user not active`() {
        doReturn(user.copy(status = UserStatus.TERMINATED)).whenever(userService).getByUsername(any(), any(), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE, ex.error.code)
    }
}
