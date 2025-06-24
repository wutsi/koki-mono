package com.wutsi.koki.chatbot.telegram.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.platform.debug.DebugRestInterceptor
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.NoAccessTokenHolder
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.tenant.TenantRestInterceptor
import com.wutsi.koki.platform.tracing.ClientProvider
import com.wutsi.koki.platform.tracing.spring.ClientRestInterceptor
import com.wutsi.koki.platform.url.BitlyUrlShortener
import com.wutsi.koki.platform.url.UrlShortener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KokiPlatformConfiguration(
    private val tenantProvider: TenantProvider,
    private val objectMapper: ObjectMapper,

    @Value("\${koki.webapp.client-id}") private val clientId: String,
    @Value("\${koki.url.bitly.api-key}") private val bitlyApiKey: String,
) {
    @Bean
    fun debugRestInterceptor(): DebugRestInterceptor {
        return DebugRestInterceptor()
    }

    @Bean
    fun tenantRestInterceptor(): TenantRestInterceptor {
        return TenantRestInterceptor(tenantProvider)
    }

    @Bean
    fun clientRestInterceptor(): ClientRestInterceptor {
        return ClientRestInterceptor(clientProvider())
    }

    @Bean
    fun clientProvider(): ClientProvider {
        return ClientProvider(clientId)
    }

    @Bean
    fun accessTokenProvider(): AccessTokenHolder {
        return NoAccessTokenHolder()
    }

    @Bean
    fun urlShortener(): UrlShortener {
        return BitlyUrlShortener(accessToken = bitlyApiKey, objectMapper = objectMapper)
    }
}
