package com.wutsi.koki.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
open class SecurityConfiguration {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SecurityConfiguration::class.java)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        LOGGER.info(">>> Configuring HttpSecurity")
        return http
            .csrf { customizer ->
                customizer.disable()
            }
            .sessionManagement { customizer ->
                customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { customizer ->
                customizer.anyRequest().permitAll()
            }
            .build()
    }

//    @Bean
//    open fun corsConfiguration(): CorsConfigurationSource {
//        val configuration = CorsConfiguration()
//        configuration.allowedOrigins = listOf("*")
//        configuration.allowedMethods = listOf("GET", "OPTIONS", "HEAD", "PUT", "POST", "DELETE")
//        configuration.allowedHeaders = listOf("*")
//        configuration.exposedHeaders = listOf("*")
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }
}
