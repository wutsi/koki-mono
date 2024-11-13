package com.wutsi.koki.portal.config

import com.wutsi.koki.portal.security.AccessTokenAuthenticationFilter
import com.wutsi.koki.portal.security.AuthenticationSuccessHandlerImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationFilter: AccessTokenAuthenticationFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { customizer ->
                customizer
                    .requestMatchers(AntPathRequestMatcher("/")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/forms")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/forms/**/*")).authenticated()
                    .anyRequest().permitAll()
            }
            .addFilterBefore(authenticationFilter, AnonymousAuthenticationFilter::class.java)
            .csrf { customizer -> customizer.disable() }
            .httpBasic { customizer -> customizer.disable() }
            .formLogin { customizer ->
                customizer
                    .loginPage("/login")
                    .successHandler(successHandler())
            }
            .build()
    }

    @Bean
    fun successHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandlerImpl()
    }
}
