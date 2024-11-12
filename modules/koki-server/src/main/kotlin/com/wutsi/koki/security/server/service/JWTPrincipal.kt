package com.wutsi.koki.security.server.service

import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date

open class JWTPrincipal(private val jwt: DecodedJWT) {
    companion object {
        const val CLAIM_USER_ID = "userId"
        const val CLAIM_TENANT_ID = "tenantId"
    }

    fun getUserId(): Long {
        return jwt.getClaim(CLAIM_USER_ID).`as`(Long::class.java)
    }

    fun getTenantId(): Long {
        return jwt.getClaim(CLAIM_TENANT_ID).`as`(Long::class.java)
    }

    fun getSubject(): String {
        return jwt.subject
    }

    fun expired(): Boolean {
        return jwt.expiresAt.before(Date())
    }
}
