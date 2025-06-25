package com.wutsi.koki.chatbot.telegram.config

import com.wutsi.koki.platform.debug.DebugRestInterceptor
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.tenant.TenantRestInterceptor
import com.wutsi.koki.platform.tracing.spring.ClientRestInterceptor
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.sdk.KokiConfiguration
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRoomLocationMetrics
import com.wutsi.koki.sdk.KokiRooms
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class KokiSDKConfiguration(
    private val tenantRestInterceptor: TenantRestInterceptor,
    private val debugRestInterceptor: DebugRestInterceptor,
    private val clientRestInterceptor: ClientRestInterceptor,
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder,

    @Value("\${koki.rest.connection-timeout}") private val connectionTimeout: Long,
    @Value("\${koki.rest.read-timeout}") private val readTimeout: Long,
    @Value("\${koki.sdk.base-url}") private val baseUrl: String,
) {
    @Bean
    fun urlBuilder(): URLBuilder {
        return URLBuilder(baseUrl)
    }

    @Bean
    fun kokiAccounts(): KokiAccounts {
        return KokiAccounts(urlBuilder(), rest())
    }

    @Bean
    fun kokiConfiguration(): KokiConfiguration {
        return KokiConfiguration(urlBuilder(), rest())
    }

    @Bean
    fun kokiRefData(): KokiRefData {
        return KokiRefData(urlBuilder(), restWithoutTenantHeader())
    }

    @Bean
    fun kokiFiles(): KokiFiles {
        return KokiFiles(urlBuilder(), rest(), tenantProvider, accessTokenHolder)
    }

    @Bean
    fun kokiRooms(): KokiRooms {
        return KokiRooms(urlBuilder(), rest())
    }

    @Bean
    fun kokiRoomLocationMetrics(): KokiRoomLocationMetrics {
        return KokiRoomLocationMetrics(urlBuilder(), rest())
    }

    @Bean
    fun kokiTenant(): KokiTenants {
        return KokiTenants(urlBuilder(), restWithoutTenantHeader())
    }

    @Bean
    @Primary
    fun rest(): RestTemplate = RestTemplateBuilder().connectTimeout(Duration.ofMillis(connectionTimeout))
        .readTimeout(Duration.ofMillis(readTimeout)).interceptors(
            debugRestInterceptor,
            clientRestInterceptor,
            tenantRestInterceptor,
        ).build()

    @Bean("RestWithoutTenantHeader")
    fun restWithoutTenantHeader(): RestTemplate =
        RestTemplateBuilder().connectTimeout(Duration.ofMillis(connectionTimeout))
            .readTimeout(Duration.ofMillis(readTimeout)).interceptors(
                debugRestInterceptor,
                clientRestInterceptor,
            ).build()

    @Bean("RestForAuthentication")
    fun restForAuthentication(): RestTemplate =
        RestTemplateBuilder().connectTimeout(Duration.ofMillis(connectionTimeout))
            .readTimeout(Duration.ofMillis(readTimeout)).interceptors(
                tenantRestInterceptor,
                debugRestInterceptor,
                clientRestInterceptor,
            ).build()
}
