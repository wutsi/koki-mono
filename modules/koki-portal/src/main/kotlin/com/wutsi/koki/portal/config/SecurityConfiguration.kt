package com.wutsi.koki.portal.config

import com.wutsi.koki.portal.security.JWTAuthenticationFilter
import com.wutsi.koki.portal.service.AccessTokenHolder
import com.wutsi.koki.security.dto.JWTDecoder
import jakarta.servlet.Filter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val accessTokenHolder: AccessTokenHolder
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { customizer ->
                customizer
                    .requestMatchers(AntPathRequestMatcher("/")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/forms")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/forms/**/*")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/workflows")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/workflows/**/*")).authenticated()
                    .anyRequest().permitAll()
            }
            .addFilterBefore(authorizationFilter(), AnonymousAuthenticationFilter::class.java)
            .csrf { customizer -> customizer.disable() }
            .httpBasic { customizer -> customizer.disable() }
            .formLogin { customizer ->
                customizer.loginPage("/login")
            }
            .build()
    }

    @Bean
    fun authorizationFilter(): Filter {
        return JWTAuthenticationFilter(accessTokenHolder, jwtDecoder())
    }

    @Bean
    fun jwtDecoder(): JWTDecoder {
        return JWTDecoder()
    }
}
