package com.wutsi.koki.email.server.config

import com.wutsi.koki.email.server.service.EmailFilterSet
import com.wutsi.koki.email.server.service.filter.EmailDecoratorFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmailFilterConfiguration(
    private val emailDecoratorFilter: EmailDecoratorFilter
) {
    @Bean
    fun emailFilterSet(): EmailFilterSet {
        return EmailFilterSet(
            filters = listOf(
                emailDecoratorFilter
            )
        )
    }
}
