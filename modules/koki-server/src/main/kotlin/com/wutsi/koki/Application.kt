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
        "com.wutsi.koki.account.server.domain",
        "com.wutsi.koki.contact.server.domain",
        "com.wutsi.koki.email.server.domain",
        "com.wutsi.koki.employee.server.domain",
        "com.wutsi.koki.file.server.domain",
        "com.wutsi.koki.form.server.domain",
        "com.wutsi.koki.invoice.server.domain",
        "com.wutsi.koki.message.server.domain",
        "com.wutsi.koki.module.server.domain",
        "com.wutsi.koki.note.server.domain",
        "com.wutsi.koki.payment.server.domain",
        "com.wutsi.koki.product.server.domain",
        "com.wutsi.koki.refdata.server.domain",
        "com.wutsi.koki.tax.server.domain",
        "com.wutsi.koki.tenant.server.domain",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.wutsi.koki.account.server.dao",
        "com.wutsi.koki.contact.server.dao",
        "com.wutsi.koki.email.server.dao",
        "com.wutsi.koki.employee.server.dao",
        "com.wutsi.koki.file.server.dao",
        "com.wutsi.koki.form.server.dao",
        "com.wutsi.koki.invoice.server.dao",
        "com.wutsi.koki.module.server.dao",
        "com.wutsi.koki.note.server.dao",
        "com.wutsi.koki.payment.server.dao",
        "com.wutsi.koki.product.server.dao",
        "com.wutsi.koki.refdata.server.dao",
        "com.wutsi.koki.tax.server.dao",
        "com.wutsi.koki.tenant.server.dao",
    ],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
