package com.wutsi.koki.security.server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.lowagie.text.pdf.PdfPKCS7.getAlgorithm
import com.wutsi.koki.security.dto.JWTDecoder
import com.wutsi.koki.security.dto.JWTPrincipal
import com.wutsi.koki.tenant.dto.UserType
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
open class AccessTokenService(
    private val jwtDecoder: JWTDecoder,
    @Value("\${koki.module.security.access-token.ttl}") private val ttl: Int,
) {
    fun create(
        application: String,
        userId: Long,
        subject: String,
        subjectType: UserType,
        tenantId: Long,
    ): String {
        val algo = getAlgorithm()
        val now = Date()
        return JWT.create()
            .withIssuer(JWTDecoder.ISSUER)
            .withClaim(JWTPrincipal.CLAIM_USER_ID, userId)
            .withClaim(JWTPrincipal.CLAIM_SUBJECT_TYPE, subjectType.name)
            .withClaim(JWTPrincipal.CLAIM_TENANT_ID, tenantId)
            .withClaim(JWTPrincipal.CLAIM_APPLICATION, application)
            .withSubject(subject)
            .withIssuedAt(now)
            .withExpiresAt(DateUtils.addSeconds(now, ttl))
            .sign(algo)
    }

    fun decode(accessToken: String): JWTPrincipal {
        return jwtDecoder.decode(accessToken)
    }

    private fun getAlgorithm(): Algorithm {
        return Algorithm.none()
    }
}
