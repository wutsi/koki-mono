package com.wutsi.koki.security.server.service

import com.wutsi.koki.security.dto.JWTPrincipal
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JWTAuthentication(private val jwtPrincipal: JWTPrincipal) : Authentication {
    private var authenticated: Boolean = true

    override fun getName(): String? {
        return jwtPrincipal.name
    }

    override fun getPrincipal(): Any? {
        return jwtPrincipal
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return emptyList()
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}
