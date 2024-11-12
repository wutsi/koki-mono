package com.wutsi.koki.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAPIConfiguration {
    companion object {
        const val SECURITY_SCHEME = "bearerAuth"
    }

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME))
            .components(
                Components()
                    .addSecuritySchemes(
                        SECURITY_SCHEME,
                        SecurityScheme()
                            .name(SECURITY_SCHEME)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
            .info(
                Info().title("Koki API")
                    .version("1.0")
            )
    }
}
