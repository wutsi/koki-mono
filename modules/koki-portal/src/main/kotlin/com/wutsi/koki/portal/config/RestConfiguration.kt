package com.wutsi.koki.portal.config

import com.wutsi.koki.portal.rest.TenantRestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestConfiguration(
    private val tenantRestInterceptor: TenantRestInterceptor,

    @Value("\${koki.rest.connection-timeout}") private val connectionTimeout: Long,
    @Value("\${koki.rest.read-timeout}") private val readTimeout: Long,
) {
    @Bean
    fun restTemplate(): RestTemplate =
        RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(connectionTimeout))
            .setReadTimeout(Duration.ofMillis(readTimeout))
            .interceptors(
                tenantRestInterceptor
            )
            .build()
}
