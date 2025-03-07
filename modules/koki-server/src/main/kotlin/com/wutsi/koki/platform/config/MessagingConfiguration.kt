package com.wutsi.koki.platform.config

import com.wutsi.koki.platform.messaging.MessagingServiceBuilder
import com.wutsi.koki.platform.messaging.smtp.SMTPMessagingServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MessagingConfiguration(
    @Value("\${koki.smtp.host}") private val host: String,
    @Value("\${koki.smtp.port}") private val port: Int,
    @Value("\${koki.smtp.username}") private val username: String,
    @Value("\${koki.smtp.password}") private val password: String,
    @Value("\${koki.smtp.from}") private val from: String,
) {
    @Bean
    open fun messagingServiceBuilder(): MessagingServiceBuilder {
        return MessagingServiceBuilder(
            smtpBuilder = smtpMessagingServiceBuilder()
        )
    }

    @Bean
    open fun smtpMessagingServiceBuilder(): SMTPMessagingServiceBuilder {
        return SMTPMessagingServiceBuilder(
            host = host,
            port = port,
            username = username,
            password = password,
            from = from,
        )
    }
}
