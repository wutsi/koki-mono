package com.wutsi.koki.config

import com.wutsi.koki.platform.security.AccessTokenHolder
import com.wutsi.koki.platform.security.RequestAccessTokenHolder
import com.wutsi.koki.platform.security.servlet.JWTAuthenticationFilter
import com.wutsi.koki.security.dto.JWTDecoder
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
open class SecurityConfiguration(
    private val request: HttpServletRequest
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SecurityConfiguration::class.java)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        LOGGER.info(">>> Configuring HttpSecurity")
        return http.csrf { customizer -> customizer.disable() }
            .sessionManagement { customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { customizer ->
                customizer.anyRequest().permitAll()
            }.build()
    }

    @Bean
    fun accessTokenHolder(): AccessTokenHolder {
        return RequestAccessTokenHolder(request)
    }

    @Bean
    fun jwtDecoder(): JWTDecoder {
        return JWTDecoder()
    }

    @Bean
    fun authenticationFilter(): JWTAuthenticationFilter {
        return JWTAuthenticationFilter(accessTokenHolder(), jwtDecoder())
    }
}
