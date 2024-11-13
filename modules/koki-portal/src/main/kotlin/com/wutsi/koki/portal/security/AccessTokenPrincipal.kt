package com.wutsi.koki.portal.security

import java.security.Principal

class AccessTokenPrincipal(private val accessToken: String) : Principal {
    override fun getName(): String? {
        return accessToken
    }
}
