package com.wutsi.koki.portal.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.sdk.KokiFiles
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.sdk.KokiLogs
import com.wutsi.koki.sdk.KokiMessages
import com.wutsi.koki.sdk.KokiScripts
import com.wutsi.koki.sdk.KokiTenant
import com.wutsi.koki.sdk.KokiUser
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.sdk.TenantProvider
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class KokiSDKConfiguration(
    private val rest: RestTemplate,
    private val tenantProvider: TenantProvider,
    private val objectMapper: ObjectMapper,

    @Value("\${koki.sdk.base-url}") private val baseUrl: String,
) {
    @Bean
    fun urlBuilder(): URLBuilder {
        return URLBuilder(baseUrl)
    }

    @Bean
    fun kokiAuthentication(): KokiAuthentication {
        return KokiAuthentication(urlBuilder(), rest)
    }

    @Bean
    fun kokiFile(): KokiFiles {
        return KokiFiles(urlBuilder(), rest)
    }

    @Bean
    fun kokiForms(): KokiForms {
        return KokiForms(urlBuilder(), rest, tenantProvider)
    }

    @Bean
    fun kokiLogs(): KokiLogs {
        return KokiLogs(urlBuilder(), rest)
    }

    @Bean
    fun kokiMessages(): KokiMessages {
        return KokiMessages(urlBuilder(), rest)
    }

    @Bean
    fun kokiScripts(): KokiScripts {
        return KokiScripts(urlBuilder(), rest)
    }

    @Bean
    fun kokiTenant(): KokiTenant {
        return KokiTenant(urlBuilder(), rest)
    }

    @Bean
    fun kokiUser(): KokiUser {
        return KokiUser(urlBuilder(), rest)
    }

    @Bean
    fun kokiWorkflow(): KokiWorkflow {
        return KokiWorkflow(urlBuilder(), rest, tenantProvider, objectMapper)
    }

    @Bean
    fun kokiWorkflowInstance(): KokiWorkflowInstance {
        return KokiWorkflowInstance(urlBuilder(), rest, tenantProvider)
    }
}
