package com.wutsi.koki

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfiguration {
    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy = FlywayMigrationStrategy { flyway ->
        if (!cleaned) {
            flyway.clean()
            cleaned = true
        }
        flyway.migrate()
    }

    companion object {
        var cleaned: Boolean = false
    }
}
