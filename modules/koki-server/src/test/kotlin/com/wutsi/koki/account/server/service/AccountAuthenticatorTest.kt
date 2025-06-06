package com.wutsi.koki.account.server.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.form.server.domain.AccountEntity
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.server.service.AccessTokenService
import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.PasswordService
import com.wutsi.koki.tenant.server.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccountAuthenticatorTest {
    private val authenticatorService = mock<AuthenticationService>()
    private val accountService = mock<AccountService>()
    private val passwordService = mock<PasswordService>()
    private val accessTokenService = mock<AccessTokenService>()
    private val userService = mock<UserService>()
    private val authenticator = AccountAuthenticator(
        authenticatorService = authenticatorService,
        accountService = accountService,
        passwordService = passwordService,
        accessTokenService = accessTokenService,
        userService = userService,
    )

    private val user = UserEntity(
        id = 333L,
        username = "ray.sponsible",
        password = "__secret__",
        status = UserStatus.ACTIVE,
        type = UserType.ACCOUNT,
        accountId = 111L,
    )

    private val account = AccountEntity(
        id = user.accountId,
        name = "Ray Sponsible",
    )

    private val accessToken = UUID.randomUUID().toString()
    private val request = LoginRequest(
        username = user.username,
        password = "secret",
        application = ApplicationName.CLIENT,
    )

    @BeforeEach
    fun setup() {
        doReturn(user).whenever(userService).getByUsername(any(), eq(UserType.ACCOUNT), any())

        doReturn(account).whenever(accountService).get(any(), any())

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
        assertFalse(authenticator.supports(request.copy(application = ApplicationName.PORTAL)))
    }

    @Test
    fun authenticate() {
        val result = authenticator.authenticate(request, user.tenantId)
        assertEquals(accessToken, result)
        verify(accessTokenService).create(
            request.application,
            user.id ?: -1,
            account.name,
            UserType.ACCOUNT,
            user.tenantId,
        )
    }

    @Test
    fun `user not found`() {
        doThrow(
            NotFoundException(error = Error(ErrorCode.USER_NOT_FOUND))
        )
            .whenever(userService).getByUsername(any(), any(), any())

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
        doReturn(user.copy(status = UserStatus.TERMINATED))
            .whenever(userService)
            .getByUsername(any(), any(), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE, ex.error.code)
    }

    @Test
    fun `account not found`() {
        doReturn(null).whenever(accountService).get(any(), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.error.code)
    }

    @Test
    fun `user has no account id`() {
        doReturn(user.copy(accountId = null))
            .whenever(userService)
            .getByUsername(any(), eq(UserType.ACCOUNT), any())

        val ex = assertThrows<ConflictException> {
            authenticator.authenticate(request, user.tenantId)
        }
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, ex.error.code)
    }
}
