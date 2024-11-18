package com.wutsi.koki.security.dto

import com.auth0.jwt.interfaces.DecodedJWT
import java.security.Principal

open class JWTPrincipal(private val jwt: DecodedJWT) : Principal {
    companion object {
        const val CLAIM_USER_ID = "userId"
        const val CLAIM_TENANT_ID = "tenantId"
    }

    override fun getName(): String? {
        return getUserId().toString()
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
}