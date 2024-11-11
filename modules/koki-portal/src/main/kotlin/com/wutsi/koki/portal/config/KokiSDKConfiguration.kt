package com.wutsi.koki.portal.config

import com.wutsi.koki.portal.rest.KokiForms
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfiguration(
    private val rest: RestTemplate,

    @Value("\${koki.sdk.base-url}") private val baseUrl: String,
) {
    @Bean
    fun formsBackend(): KokiForms {
        return KokiForms(baseUrl, rest)
    }
}
