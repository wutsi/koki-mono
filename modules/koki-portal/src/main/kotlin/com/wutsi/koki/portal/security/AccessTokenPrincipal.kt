package com.wutsi.koki.portal.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class AccessTokenAuthentication(private val accessToken: String) : Authentication {
    private var authenticated: Boolean = true

    override fun getName(): String? {
        return accessToken
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
