package com.wutsi.koki.platform.config

import com.github.mustachejava.DefaultMustacheFactory
import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.TemplatingEngine
import com.wutsi.koki.platform.messaging.mustache.MustacheMessagingTemplateEngine
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MessagingConfiguration {
    @Bean
    open fun messagingServiceBuilder(): MessagingServiceBuilder {
        return MessagingServiceBuilder(
            smtpBuilder = smtpMessagingServiceBuilder()
        )
    }

    @Bean
    open fun smtpMessagingServiceBuilder(): SMTPMessagingServiceBuilder {
        return SMTPMessagingServiceBuilder()
    }

    @Bean
    open fun messagingTemplateEngine(): TemplatingEngine {
        return MustacheMessagingTemplateEngine(DefaultMustacheFactory())
    }
}
