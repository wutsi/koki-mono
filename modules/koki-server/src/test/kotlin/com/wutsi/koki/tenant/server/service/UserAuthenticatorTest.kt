package com.wutsi.koki.tenant.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.server.service.AccessTokenService
import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.security.server.service.UserAuthenticator
import com.wutsi.koki.tenant.dto.UserStatus
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
    private val passwordEncryptor = mock<PasswordEncryptor>()
    private val accessTokenService = mock<AccessTokenService>()
    private val authenticator = UserAuthenticator(
        authenticatorService = authenticatorService,
        userService = userService,
        passwordEncryptor = passwordEncryptor,
        accessTokenService = accessTokenService,
    )

    private val user = UserEntity(
        id = 111,
        tenantId = 222,
        username = "ray.sponsible",
        displayName = "Ray Sponsible",
        email = "ray.sponsible@gmail.com",
        status = UserStatus.ACTIVE,
        salt = UUID.randomUUID().toString()
    )

    private val accessToken = UUID.randomUUID().toString()

    private val request = LoginRequest(
        username = user.username,
        password = "secret",
        application = ApplicationName.PORTAL,
    )

    @BeforeEach
    fun setup() {
        doReturn(listOf(user)).whenever(userService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )
        doReturn(accessToken).whenever(accessTokenService).create(any(), any(), any(), any())
        doReturn(true).whenever(passwordEncryptor).matches(any(), any(), any())
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
            user.tenantId,
        )
    }

    @Test
    fun `user not found`() {
        doReturn(emptyList<UserEntity>()).whenever(userService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.error.code)
    }

    @Test
    fun `password mismatch`() {
        doReturn(false).whenever(passwordEncryptor).matches(any(), any(), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.error.code)
    }

    @Test
    fun `user not active`() {
        doReturn(
            listOf(user.copy(status = UserStatus.SUSPENDED))
        ).whenever(userService).search(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
        )

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE, ex.error.code)
    }
}
