package com.wutsi.koki.portal.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.rest.AuthorizationInterceptor
import com.wutsi.koki.portal.rest.DebugRestInterceptor
import com.wutsi.koki.portal.rest.TenantRestInterceptor
import com.wutsi.koki.sdk.AccessTokenProvider
import com.wutsi.koki.sdk.KokiAccounts
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.sdk.KokiConfiguration
import com.wutsi.koki.sdk.KokiContacts
import com.wutsi.koki.sdk.KokiEmails
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.sdk.KokiLogs
import com.wutsi.koki.sdk.KokiMessages
import com.wutsi.koki.sdk.KokiModules
import com.wutsi.koki.sdk.KokiNotes
import com.wutsi.koki.sdk.KokiScripts
import com.wutsi.koki.sdk.KokiServices
import com.wutsi.koki.sdk.KokiTaxes
import com.wutsi.koki.sdk.KokiTenants
import com.wutsi.koki.sdk.KokiUsers
import com.wutsi.koki.sdk.KokiWorkflowInstances
import com.wutsi.koki.sdk.KokiWorkflows
import com.wutsi.koki.sdk.TenantProvider
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class KokiSDKConfiguration(
    private val tenantProvider: TenantProvider,
    private val accessTokenProvider: AccessTokenProvider,
    private val objectMapper: ObjectMapper,
    private val tenantRestInterceptor: TenantRestInterceptor,
    private val authorizationInterceptor: AuthorizationInterceptor,
    private val debugRestInterceptor: DebugRestInterceptor,

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
        return KokiAuthentication(urlBuilder(), rest())
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
    fun kokiEmails(): KokiEmails {
        return KokiEmails(urlBuilder(), rest())
    }

    @Bean
    fun kokiFile(): KokiFiles {
        return KokiFiles(urlBuilder(), rest(), tenantProvider, accessTokenProvider)
    }

    @Bean
    fun kokiForms(): KokiForms {
        return KokiForms(urlBuilder(), rest(), tenantProvider)
    }

    @Bean
    fun kokiLogs(): KokiLogs {
        return KokiLogs(urlBuilder(), rest())
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
    fun kokiScripts(): KokiScripts {
        return KokiScripts(urlBuilder(), rest())
    }

    @Bean
    fun kokiServices(): KokiServices {
        return KokiServices(urlBuilder(), rest())
    }

    @Bean
    fun kokiTaxes(): KokiTaxes {
        return KokiTaxes(urlBuilder(), rest())
    }

    @Bean
    fun kokiTenant(): KokiTenants {
        return KokiTenants(urlBuilder(), restWithoutTenantHeader())
    }

    @Bean
    fun kokiUser(): KokiUsers {
        return KokiUsers(urlBuilder(), rest())
    }

    @Bean
    fun kokiWorkflow(): KokiWorkflows {
        return KokiWorkflows(urlBuilder(), rest(), tenantProvider, objectMapper)
    }

    @Bean
    fun kokiWorkflowInstance(): KokiWorkflowInstances {
        return KokiWorkflowInstances(urlBuilder(), rest(), tenantProvider)
    }

    @Bean
    fun rest(): RestTemplate =
        RestTemplateBuilder()
            .connectTimeout(Duration.ofMillis(connectionTimeout))
            .readTimeout(Duration.ofMillis(readTimeout))
            .interceptors(
                debugRestInterceptor,
                tenantRestInterceptor,
                authorizationInterceptor,
            )
            .build()

    private fun restWithoutTenantHeader(): RestTemplate =
        RestTemplateBuilder()
            .connectTimeout(Duration.ofMillis(connectionTimeout))
            .readTimeout(Duration.ofMillis(readTimeout))
            .interceptors(
                debugRestInterceptor,
                authorizationInterceptor,
            )
            .build()
}
