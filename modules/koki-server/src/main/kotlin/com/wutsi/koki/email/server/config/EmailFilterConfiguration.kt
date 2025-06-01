package com.wutsi.koki.email.server.config

import com.wutsi.koki.email.server.service.EmailFilterSet
import com.wutsi.koki.email.server.service.filter.CssFilter
import com.wutsi.koki.email.server.service.filter.EmailDecoratorFilter
import com.wutsi.koki.email.server.service.filter.HtmlEscapeFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EmailFilterConfiguration(
    private val emailDecoratorFilter: EmailDecoratorFilter,
    private val htmlEscapeFilter: HtmlEscapeFilter,
    private val cssFilter: CssFilter,
) {
    @Bean
    fun emailFilterSet(): EmailFilterSet {
        return EmailFilterSet(
            filters = listOf(
                cssFilter,
                emailDecoratorFilter, // Must be before the last
                htmlEscapeFilter, // Must be the last
            )
        )
    }
}
