package com.wutsi.koki.security.server.service

import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.security.dto.LoginRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
open class AuthenticationService {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(AuthenticationService::class.java)
    }

    private val authenticators = mutableListOf<Authenticator>()

    fun register(authenticator: Authenticator) {
        LOGGER.info(">>> Registering authenticator: $authenticator")
        authenticators.add(authenticator)
    }

    fun unregister(authenticator: Authenticator) {
        LOGGER.info(">>> Unregistering authenticator: $authenticator")
        authenticators.remove(authenticator)
    }

    fun authenticate(request: LoginRequest, tenantId: Long): String {
        return authenticators.find { authenticator -> authenticator.supports(request) }
            ?.authenticate(request, tenantId)
            ?: throw ConflictException(Error(ErrorCode.AUTHENTICATION_NO_AUTHENTICATOR))
    }
}
