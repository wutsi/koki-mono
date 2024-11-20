package com.wutsi.koki.portal.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.koki.portal.service.TenantService
import com.wutsi.koki.sdk.KokiAuthentication
import com.wutsi.koki.sdk.KokiFormData
import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.sdk.KokiUser
import com.wutsi.koki.sdk.KokiWorkflow
import com.wutsi.koki.sdk.KokiWorkflowEngine
import com.wutsi.koki.sdk.KokiWorkflowInstance
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class KokiSDKConfiguration(
    private val rest: RestTemplate,
    private val tenantService: TenantService,
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
    fun kokiForms(): KokiForms {
        return KokiForms(urlBuilder(), rest, tenantService)
    }

    @Bean
    fun kokiFormData(): KokiFormData {
        return KokiFormData(urlBuilder(), rest)
    }

    @Bean
    fun kokiUser(): KokiUser {
        return KokiUser(urlBuilder(), rest)
    }

    @Bean
    fun kokiWorkflow(): KokiWorkflow {
        return KokiWorkflow(urlBuilder(), rest, tenantService, objectMapper)
    }

    @Bean
    fun kokiWorkflowInstance(): KokiWorkflowInstance {
        return KokiWorkflowInstance(urlBuilder(), rest, tenantService)
    }

    @Bean
    fun kokiWorkflowEngine(): KokiWorkflowEngine {
        return KokiWorkflowEngine(urlBuilder(), rest)
    }
}
