package com.wutsi.koki.portal.config

import com.wutsi.koki.platform.debug.DebugRestInterceptor
import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.AuthorizationRestInterceptor
import com.wutsi.koki.platform.tenant.TenantProvider
import com.wutsi.koki.platform.tenant.TenantRestInterceptor
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.sdk.KokiConfiguration
import com.wutsi.koki.sdk.KokiContacts
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiInvitations
import com.wutsi.koki.sdk.KokiListings
import com.wutsi.koki.sdk.KokiMessages
import com.wutsi.koki.sdk.KokiModules
import com.wutsi.koki.sdk.KokiNotes
import com.wutsi.koki.sdk.KokiOffer
import com.wutsi.koki.sdk.KokiOfferVersion
import com.wutsi.koki.sdk.KokiRefData
import com.wutsi.koki.sdk.KokiRoles
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.sdk.KokiTypes
import com.wutsi.koki.sdk.KokiUsers
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
    private val tenantProvider: TenantProvider,
    private val accessTokenHolder: AccessTokenHolder,
    private val tenantRestInterceptor: TenantRestInterceptor,
    private val debugRestInterceptor: DebugRestInterceptor,
    private val authorizationRestInterceptor: AuthorizationRestInterceptor,

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
    fun kokiAuthentication(): KokiAuthentication {
        return KokiAuthentication(urlBuilder(), restForAuthentication())
    }

    @Bean
    fun kokiConfiguration(): KokiConfiguration {
        return KokiConfiguration(urlBuilder(), rest())
    }

    @Bean
    fun kokiContacts(): KokiContacts {
        return KokiContacts(urlBuilder(), rest())
    }

    @Bean
    fun kokiFiles(): KokiFiles {
        return KokiFiles(urlBuilder(), rest(), tenantProvider, accessTokenHolder)
    }

    @Bean
    fun kokiInvitations(): KokiInvitations {
        return KokiInvitations(urlBuilder(), rest())
    }

    @Bean
    fun kokiListings(): KokiListings {
        return KokiListings(urlBuilder(), rest())
    }

    @Bean
    fun kokiMessages(): KokiMessages {
        return KokiMessages(urlBuilder(), rest())
    }

    @Bean
    fun kokiModules(): KokiModules {
        return KokiModules(urlBuilder(), restWithoutTenantHeader())
    }

    @Bean
    fun kokiNotes(): KokiNotes {
        return KokiNotes(urlBuilder(), rest())
    }

    @Bean
    fun kokiOffer(): KokiOffer {
        return KokiOffer(urlBuilder(), rest())
    }

    @Bean
    fun kokiOfferVersion(): KokiOfferVersion {
        return KokiOfferVersion(urlBuilder(), rest())
    }

    @Bean
    fun kokiRefData(): KokiRefData {
        return KokiRefData(urlBuilder(), restWithoutTenantHeader())
    }

    @Bean
    fun kokiRole(): KokiRoles {
        return KokiRoles(urlBuilder(), rest())
    }

    @Bean
    fun kokiTenant(): KokiTenants {
        return KokiTenants(urlBuilder(), restWithoutTenantHeader())
    }

    @Bean
    fun kokiTypes(): KokiTypes {
        return KokiTypes(urlBuilder(), rest())
    }

    @Bean
    fun kokiUser(): KokiUsers {
        return KokiUsers(urlBuilder(), rest())
    }

    @Bean
    @Primary
    fun rest(): RestTemplate = RestTemplateBuilder().connectTimeout(Duration.ofMillis(connectionTimeout))
        .readTimeout(Duration.ofMillis(readTimeout)).interceptors(
            debugRestInterceptor,
            tenantRestInterceptor,
            authorizationRestInterceptor,
        ).build()

    @Bean("RestWithoutTenantHeader")
    fun restWithoutTenantHeader(): RestTemplate =
        RestTemplateBuilder().connectTimeout(Duration.ofMillis(connectionTimeout))
            .readTimeout(Duration.ofMillis(readTimeout)).interceptors(
                debugRestInterceptor,
//                authorizationRestInterceptor,
            ).build()

    @Bean("RestForAuthentication")
    fun restForAuthentication(): RestTemplate =
        RestTemplateBuilder().connectTimeout(Duration.ofMillis(connectionTimeout))
            .readTimeout(Duration.ofMillis(readTimeout)).interceptors(
                tenantRestInterceptor,
                debugRestInterceptor,
            ).build()
}
