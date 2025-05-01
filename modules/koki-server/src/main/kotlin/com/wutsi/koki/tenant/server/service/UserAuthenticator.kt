package com.wutsi.koki.security.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.security.dto.LoginRequest
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.dto.UserType
import com.wutsi.koki.tenant.server.service.PasswordService
import com.wutsi.koki.tenant.server.service.UserService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service

@Service
class UserAuthenticator(
    private val authenticatorService: AuthenticationService,
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val accessTokenService: AccessTokenService,
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
        return request.application == ApplicationName.PORTAL
    }

    override fun authenticate(request: LoginRequest, tenantId: Long): String {
        try {
            val user = userService.getByUsername(request.username, UserType.EMPLOYEE, tenantId)
            if (user.status != UserStatus.ACTIVE) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE))
            }
            if (!passwordService.matches(request.password, user.password, user.salt)) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED))
            }
            return accessTokenService.create(
                application = request.application,
                userId = user.id ?: -1,
                subject = user.displayName,
                subjectType = UserType.EMPLOYEE,
                tenantId = tenantId
            )
        } catch (ex: NotFoundException) {
            throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED), ex)
        }
    }
}
