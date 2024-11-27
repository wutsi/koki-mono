package com.wutsi.koki

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableCaching
@EntityScan(
    basePackages = [
        "com.wutsi.koki.file.server.domain",
        "com.wutsi.koki.form.server.domain",
        "com.wutsi.koki.tenant.server.domain",
        "com.wutsi.koki.workflow.server.domain",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.wutsi.koki.file.server.dao",
        "com.wutsi.koki.form.server.dao",
        "com.wutsi.koki.tenant.server.dao",
        "com.wutsi.koki.workflow.server.dao",
    ],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
