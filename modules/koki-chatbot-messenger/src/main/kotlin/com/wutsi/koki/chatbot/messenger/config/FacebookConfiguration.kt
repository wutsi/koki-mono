package com.wutsi.koki.chatbot.messenger.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class FacebookConfiguration {
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient.newHttpClient()
    }
}
