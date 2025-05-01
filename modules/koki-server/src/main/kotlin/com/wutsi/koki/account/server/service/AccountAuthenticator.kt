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
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.service.PasswordService
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jdk.internal.joptsimple.internal.Messages.message
import org.springframework.stereotype.Service

@Service
class AccountAuthenticator(
    private val userService: UserService,
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
            val user = userService.getByUsername(request.username, UserType.ACCOUNT, tenantId)
            if (user.status != UserStatus.ACTIVE) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE))
            }
            if (!passwordService.matches(request.password, user.password, user.salt)) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED))
            }
            val account = accountService.search(
                userIds = listOf(user.id ?: -1),
                tenantId = user.tenantId,
                limit = 1,
            ).firstOrNull()
                ?: throw ConflictException(
                    error = Error(
                        code = ErrorCode.AUTHENTICATION_FAILED,
                        message = "No account associated with the user"
                    )
                )

            return accessTokenService.create(
                application = request.application,
                userId = user.id ?: -1,
                subject = account.name,
                subjectType = UserType.ACCOUNT,
                tenantId = tenantId
            )
        } catch (ex: NotFoundException) {
            throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED), ex)
        }
    }
}
