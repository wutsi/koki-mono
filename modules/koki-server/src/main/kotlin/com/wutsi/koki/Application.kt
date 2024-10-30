package com.wutsi.koki

import com.wutsi.platform.core.WutsiApplication
import com.wutsi.platform.payment.EnableWutsiPayment
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
@WutsiApplication
@EnableWutsiPayment
@EntityScan(
    basePackages = [
        "com.wutsi.koki.tenant.server.domain",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.wutsi.koki.tenant.server.dao",
    ],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
