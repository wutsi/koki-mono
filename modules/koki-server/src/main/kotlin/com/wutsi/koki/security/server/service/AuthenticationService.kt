package com.wutsi.koki.security.server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.wutsi.koki.error.dto.Error
import com.wutsi.koki.error.dto.ErrorCode
import com.wutsi.koki.error.exception.ConflictException
import com.wutsi.koki.error.exception.NotFoundException
import com.wutsi.koki.tenant.server.domain.UserEntity
import com.wutsi.koki.tenant.server.service.PasswordService
import com.wutsi.koki.tenant.server.service.UserService
import org.apache.commons.lang3.time.DateUtils
import org.springframework.stereotype.Service
import java.util.Date

@Service
open class AuthenticationService(
    private val userService: UserService,
    private val passwordService: PasswordService,
) {
    companion object {
        const val ISSUER = "Koki"
    }

    fun authenticate(email: String, password: String, tenantId: Long): String {
        try {
            val user = userService.getByEmail(email, tenantId)
            if (!passwordService.matches(password, user.password, user.salt)) {
                throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED))
            }
            return createAccessToken(user)
        } catch (ex: NotFoundException) {
            throw ConflictException(error = Error(ErrorCode.AUTHENTICATION_FAILED), ex)
        }
    }

    fun decodeAccessToken(token: String): JWTPrincipal {
        val verifier = JWT.require(getAlgorithm())
            .withIssuer(ISSUER)
            .build()
        return JWTPrincipal(verifier.verify(token))
    }

    fun createAccessToken(user: UserEntity, ttlSeconds: Int = 86400): String {
        val algo = getAlgorithm()
        val now = Date()
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(user.displayName)
            .withClaim(JWTPrincipal.CLAIM_USER_ID, user.id)
            .withClaim(JWTPrincipal.CLAIM_TENANT_ID, user.tenant.id)
            .withIssuedAt(now)
            .withExpiresAt(DateUtils.addSeconds(now, ttlSeconds))
            .sign(algo)
    }

    private fun getAlgorithm(): Algorithm {
        return Algorithm.none()
    }
}
