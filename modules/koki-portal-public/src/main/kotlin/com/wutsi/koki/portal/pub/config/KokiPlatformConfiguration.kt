package com.wutsi.koki.portal.pub.config

import com.wutsi.koki.platform.debug.DebugRestInterceptor
import com.wutsi.koki.platform.logger.KVLogger
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.AuthorizationRestInterceptor
import com.wutsi.koki.platform.security.CookieAccessTokenHolder
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.tenant.TenantRestInterceptor
import com.wutsi.koki.platform.tracing.ClientProvider
import com.wutsi.koki.platform.tracing.CookieDeviceIdProvider
import com.wutsi.koki.platform.tracing.DeviceIdProvider
import com.wutsi.koki.platform.tracing.servlet.DeviceIdFilter
import com.wutsi.koki.platform.tracing.spring.ClientRestInterceptor
import com.wutsi.koki.platform.tracing.spring.DeviceIdRestInterceptor
import com.wutsi.koki.platform.tracking.ChannelTypeDetector
import com.wutsi.koki.platform.tracking.ChannelTypeProvider
import com.wutsi.koki.platform.tracking.CookieChannelTypeProvider
import com.wutsi.koki.platform.tracking.servlet.ChannelTypeFilter
import com.wutsi.koki.platform.util.Moment
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class KokiPlatformConfiguration(
    private val tenantProvider: TenantProvider,
    private val request: HttpServletRequest,
    private val response: HttpServletResponse,
    private val logger: KVLogger,
    private val clock: Clock,

    @param:Value("\${koki.webapp.client-id}") private val clientId: String,
    @param:Value("\${koki.webapp.base-url}") private val serverUrl: String,
) {
    companion object {
        const val COOKIE_ACCESS_TOKEN = "__atk"
        const val COOKIE_DEVICE_ID = "__did"
        const val COOKIE_CHANNEL_TYPE = "__chn"
        const val TTL = 86400
    }

    @Bean
    fun moment(): Moment {
        return Moment(clock)
    }

    @Bean
    fun accessTokenHolder(): AccessTokenHolder {
        return CookieAccessTokenHolder(COOKIE_ACCESS_TOKEN, TTL, request, response)
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

    @Bean
    fun clientRestInterceptor(): ClientRestInterceptor {
        return ClientRestInterceptor(clientProvider())
    }

    @Bean
    fun deviceIdRestInterceptor(): DeviceIdRestInterceptor {
        return DeviceIdRestInterceptor(deviceIdProvider(), request)
    }

    @Bean
    fun deviceIdFilter(): DeviceIdFilter {
        return DeviceIdFilter(deviceIdProvider())
    }

    @Bean
    fun deviceIdProvider(): DeviceIdProvider {
        return CookieDeviceIdProvider(COOKIE_DEVICE_ID)
    }

    @Bean
    fun clientProvider(): ClientProvider {
        return ClientProvider(clientId)
    }

    @Bean
    fun channelTypeProvider(): ChannelTypeProvider {
        return CookieChannelTypeProvider(COOKIE_CHANNEL_TYPE)
    }

    @Bean
    fun channelTypeDetector(): ChannelTypeDetector {
        return ChannelTypeDetector()
    }

    @Bean
    fun channelTypeFilter(): ChannelTypeFilter {
        return ChannelTypeFilter(
            channelTypeDetector(),
            logger,
            channelTypeProvider(),
            serverUrl
        )
    }
}
