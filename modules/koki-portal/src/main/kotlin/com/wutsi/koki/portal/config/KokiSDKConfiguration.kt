package com.wutsi.koki.portal.config

import com.wutsi.koki.sdk.KokiForms
import com.wutsi.koki.sdk.KokiWorkflowEngine
import com.wutsi.koki.sdk.URLBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class KokiSDKConfiguration(
    private val rest: RestTemplate,

    @Value("\${koki.sdk.base-url}") private val baseUrl: String,
) {
    @Bean
    fun urlBuilder(): URLBuilder {
        return URLBuilder(baseUrl)
    }

    @Bean
    fun kokiForms(): KokiForms {
        return KokiForms(urlBuilder(), rest)
    }

    @Bean
    fun kokiWorkflowEngine(): KokiWorkflowEngine {
        return KokiWorkflowEngine(urlBuilder(), rest)
    }
}
