package com.wutsi.koki.common.logger.config

import com.wutsi.koki.common.logger.DefaultKVLogger
import com.wutsi.koki.common.logger.DynamicKVLogger
import com.wutsi.koki.common.logger.KVLogger
import com.wutsi.koki.common.logger.servlet.KVLoggerFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.core.Ordered

@Configuration
open class LoggingConfiguration(
    private val context: ApplicationContext,
) {
    @Bean
    open fun loggingFilter(): FilterRegistrationBean<KVLoggerFilter> {
        val filter = FilterRegistrationBean(KVLoggerFilter(logger()))
        filter.order = Ordered.LOWEST_PRECEDENCE
        return filter
    }

    @Bean
    open fun logger(): KVLogger =
        DynamicKVLogger(context)

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    open fun requestLogger(): DefaultKVLogger =
        DefaultKVLogger()
}
