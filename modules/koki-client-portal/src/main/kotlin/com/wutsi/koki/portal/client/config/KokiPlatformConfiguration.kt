package com.wutsi.koki.portal.client.config

import com.wutsi.koki.platform.debug.DebugRestInterceptor
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.AuthorizationRestInterceptor
import com.wutsi.koki.platform.security.CookieAccessTokenHolder
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.tenant.TenantRestInterceptor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KokiPlatformConfiguration(
    private val tenantProvider: TenantProvider,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
) {
    companion object {
        const val COOKIE = "__atk"
        const val TTL = 86400
    }

    @Bean
    fun accessTokenHolder(): AccessTokenHolder {
        return CookieAccessTokenHolder(COOKIE, TTL, request, response)
    }

    @Bean
    fun debugRestInterceptor(): DebugRestInterceptor {
        return DebugRestInterceptor()
    }

    @Bean
    fun tenantRestInterceptor(): TenantRestInterceptor {
        return TenantRestInterceptor(tenantProvider)
    }

    @Bean
    fun authorizationRestInterceptor(): AuthorizationRestInterceptor {
        return AuthorizationRestInterceptor(accessTokenHolder())
    }
}
