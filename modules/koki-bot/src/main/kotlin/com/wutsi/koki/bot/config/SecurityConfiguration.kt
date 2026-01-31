package com.wutsi.koki.bot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
open class SecurityConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { customizer -> customizer.disable() }
            .sessionManagement { customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { customizer ->
                customizer.anyRequest().permitAll()
            }.build()
    }
}
