package com.wutsi.koki.security.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.party.dto.LoginRequest
import com.wutsi.koki.security.dto.ApplicationName
import com.wutsi.koki.tenant.dto.UserStatus
import com.wutsi.koki.tenant.server.service.PasswordService
import com.wutsi.koki.tenant.server.service.UserService

abstract class EmailPasswordAuthenticator(
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val accessTokenService: AccessTokenService,
) : Authenticator {
    override fun authenticate(request: LoginRequest, tenantId: Long): String {
        try {
            val user = userService.getByEmail(request.email, request.userType, tenantId)
            if (user.status != UserStatus.ACTIVE) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_USER_NOT_ACTIVE))
            }
            if (user.type != request.userType) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED))
            }
            if (!passwordService.matches(request.password, user.password, user.salt)) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED))
            }
            return accessTokenService.create(
                application = request.application,
                userId = user.id ?: -1,
                userType = request.userType,
                subject = user.displayName,
                tenantId = tenantId
            )
        } catch (ex: NotFoundException) {
            throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED), ex)
        }
    }
}
