package com.wutsi.koki.account.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.security.server.service.AccessTokenService
import com.wutsi.koki.security.server.service.AuthenticationService
import com.wutsi.koki.security.server.service.Authenticator
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.service.PasswordService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service

@Service
class AccountAuthenticator(
    private val accountUserService: AccountUserService,
    private val passwordService: PasswordService,
    private val accessTokenService: AccessTokenService,
    private val authenticatorService: AuthenticationService,
    private val accountService: AccountService,
) : Authenticator {
    @PostConstruct
    fun init() {
        authenticatorService.register(this)
    }

    @PreDestroy
    fun destroy() {
        authenticatorService.unregister(this)
    }

    override fun supports(request: LoginRequest): Boolean {
        return request.application == ApplicationName.CLIENT
    }

    override fun authenticate(request: LoginRequest, tenantId: Long): String {
        try {
            val user = accountUserService.getByUsernameOrNull(request.username, tenantId)
            if (user == null) {
                throw ConflictException(error = Error(ErrorCode.USER_NOT_FOUND))
            }
            if (user.status != UserStatus.ACTIVE) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE))
            }
            if (!passwordService.matches(request.password, user.password, user.salt)) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED))
            }
            val account = accountService.get(user.accountId, user.tenantId)
            return accessTokenService.create(
                application = request.application,
                userId = user.id ?: -1,
                subject = account.name,
                subjectType = "ACCOUNT",
                tenantId = tenantId
            )
        } catch (ex: NotFoundException) {
            throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED), ex)
        }
    }
}
